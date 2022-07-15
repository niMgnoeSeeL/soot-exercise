import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.transform.Source;

import soot.*;
import soot.jimple.Jimple;
import soot.options.Options;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.graph.pdg.EnhancedUnitGraph;
import soot.toolkits.graph.pdg.HashMutablePDG;
import soot.toolkits.graph.pdg.IRegion;
import soot.toolkits.graph.pdg.PDGNode;
import soot.toolkits.graph.pdg.PDGRegion;
import soot.toolkits.graph.pdg.ProgramDependenceGraph;
import soot.toolkits.scalar.SimpleLiveLocals;

public class App {

    public void liveLocalAnalysis(SootClass sClass) {
        Iterator methodIt = sClass.getMethods().iterator();
        while (methodIt.hasNext()) {
            SootMethod m = (SootMethod) methodIt.next();
            if (!m.getName().equals("compare")) {
                continue;
            }
            Body b = m.retrieveActiveBody();

            System.out.println("=================================");
            System.out.println("Method: " + m.getName());

            UnitGraph graph = new ExceptionalUnitGraph(b);
            SimpleLiveLocals sll = new SimpleLiveLocals(graph);

            Iterator gIt = graph.iterator();
            while (gIt.hasNext()) {
                Unit u = (Unit) gIt.next();
                List before = sll.getLiveLocalsBefore(u);
                List after = sll.getLiveLocalsAfter(u);
                UnitPrinter up = new NormalUnitPrinter(b);
                up.setIndent("");

                System.out.println("---------------------------------");

                u.toString(up);
                System.out.println(up.output());
                System.out.print("Live in: {");
                String sep = "";
                Iterator befIt = before.iterator();
                while (befIt.hasNext()) {
                    Local l = (Local) befIt.next();
                    System.out.print(sep + l.getName() + ": " + l.getType());
                    sep = ", ";
                }
                System.out.println("}");
                System.out.print("Live out: {");
                sep = "";
                Iterator aftIt = after.iterator();
                while (aftIt.hasNext()) {
                    Local l = (Local) aftIt.next();
                    System.out.print(sep + l.getName() + ": " + l.getType());
                    sep = ", ";
                }
                System.out.println("}");

                System.out.println("---------------------------------");
            }
        }
        System.out.println("=================================");
    }

    public static void dependenceAnalysis(SootClass sClass) {
        Iterator methodIt = sClass.getMethods().iterator();
        while (methodIt.hasNext()) {
            SootMethod m = (SootMethod) methodIt.next();
            if (!m.getName().equals("compare")) {
                continue;
            }

            Body b = m.retrieveActiveBody();

            System.out.println("=================================");
            System.out.println("Method: " + m.getName());

            UnitGraph graph = new EnhancedUnitGraph(b);
            HashMutablePDG pdg = new HashMutablePDG(graph);

            Queue<PDGRegion> rq = new LinkedList<>();
            rq.add((PDGRegion) pdg.GetStartRegion());
            while (!rq.isEmpty()) {
                PDGRegion r = rq.remove();
                System.out.println(r.toString());
                Iterator<PDGNode> nit = r.iterator();
                while (nit.hasNext()) {
                    PDGNode n = nit.next();
                    System.out.println(
                            Stream.of(n.toString().split("\n")).map(s -> "\t" + s).collect(Collectors.joining("\n")));
                    System.out.println();
                }
                for (IRegion childR : r.getChildRegions()) {
                    rq.add((PDGRegion) childR);
                }
            }
        }
    }

    public static void saveJimple(SootClass sClass) throws IOException {
        String fileName = SourceLocator.v().getFileNameFor(sClass, Options.output_format_jimple);
        OutputStream streamOut = new FileOutputStream(fileName);
        PrintWriter writerOut = new PrintWriter(
                new OutputStreamWriter(streamOut));
        Printer.v().printTo(sClass, writerOut);
        writerOut.flush();
        streamOut.close();
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Hello, World!");
        Options.v().set_prepend_classpath(true);
        Options.v().set_soot_classpath("target");
        System.out.println(Options.v().soot_classpath());

        SootClass sClass = Scene.v().loadClassAndSupport("benchmark.Smallest");
        Scene.v().loadNecessaryClasses();
        sClass.setApplicationClass();

        // app.liveLocalAnalysis(sClass);
        App.saveJimple(sClass);
        App.dependenceAnalysis(sClass);

    }
}