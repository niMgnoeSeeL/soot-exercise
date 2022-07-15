package com.example;

import soot.*;
import soot.jimple.*;
import soot.options.Options;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.SimpleLiveLocals;
import soot.util.*;
import java.io.*;
import java.util.*;

/**
 * Hello world!
 *
 */
public class App {

    void genHelloWorld() throws IOException {
        Scene.v().loadClassAndSupport("java.lang.Object");
		Scene.v().loadClassAndSupport("java.lang.System");
        Scene.v().loadNecessaryClasses();
		
		// Create the class HelloWorld as a public class that extends Object
		SootClass sClass = new SootClass("HelloWorld", Modifier.PUBLIC);
		sClass.setSuperclass(Scene.v().getSootClass("java.lang.Object"));
		Scene.v().addClass(sClass);
		
		// Create: public static void main(String[])
		SootMethod mainMethod = new SootMethod("main",
				Arrays.asList(new Type[] {ArrayType.v(RefType.v("java.lang.String"), 1)}),
				VoidType.v(), Modifier.PUBLIC | Modifier.STATIC);
		sClass.addMethod(mainMethod);
		
		// Generate dava body from the jimple body
		JimpleBody body = Jimple.v().newBody(mainMethod);		
		
		// Create a local to hold the main method argument
		// Note: In general for any use of objects or basic-types, must generate a local to
		// hold that in the method body
		Local frm1 = Jimple.v().newLocal("frm1", ArrayType.v(RefType.v("java.lang.String"), 1));
		body.getLocals().add(frm1);
		
		// Create a local to hold the PrintStream System.out
		Local tmpRef = Jimple.v().newLocal("tmpRef", RefType.v("java.io.PrintStream"));
		body.getLocals().add(tmpRef);
		
		// Create a unit (or statement) that assigns main's formal param into the local arg
		Chain units = body.getUnits();
		units.add(Jimple.v().newIdentityStmt(frm1,
				Jimple.v().newParameterRef(ArrayType.v
						(RefType.v("java.lang.String"), 1), 0)));
		
		// Create a unit that assigns System.out to the local tmpRef
		units.add(Jimple.v().newAssignStmt(tmpRef,
				Jimple.v().newStaticFieldRef(Scene.v().getField
						("<java.lang.System: java.io.PrintStream out>").makeRef())));
		
		// Create the call to tmpRef.println("Hello world!")
		SootMethod toCall = Scene.v().getMethod
			("<java.io.PrintStream: void println(java.lang.String)>");
		units.add(Jimple.v().newInvokeStmt
				(Jimple.v().newVirtualInvokeExpr
						(tmpRef, toCall.makeRef(), StringConstant.v("Hello world!"))));
		
		// Add an empty return statement
		units.add(Jimple.v().newReturnVoidStmt());
		
		// Set the jimple body as the active one
		mainMethod.setActiveBody(body);
		
		String fileName = SourceLocator.v().getFileNameFor(sClass, Options.output_format_class);
        System.out.println(fileName);
        OutputStream streamOut = new JasminOutputStream(
                                    new FileOutputStream(fileName));
        PrintWriter writerOut = new PrintWriter(
                                    new OutputStreamWriter(streamOut));
        JasminClass jasminClass = new soot.jimple.JasminClass(sClass);
        jasminClass.print(writerOut);
        writerOut.flush();
        streamOut.close();
    }

    public void runLiveAnalysis() {
        SootClass sClass = Scene.v().loadClassAndSupport("smallest");
        sClass.setApplicationClass();

        Iterator methodIt = sClass.getMethods().iterator();
        while (methodIt.hasNext()) {
            SootMethod m = (SootMethod) methodIt.next();
            Body b = m.retrieveActiveBody();

            System.out.println("=================================");
            System.out.println("Method: " + m.getName());

            UnitGraph graph = new ExceptionalUnitGraph(b);
            SimpleLiveLocals sll = new SimpleLiveLocals(graph);

            Iterator gIt = graph.iterator();
            while(gIt.hasNext()) {
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
                while(befIt.hasNext()) {
                    Local l = (Local) befIt.next();
                    System.out.print(sep + l.getName() + ": " + l.getType());
                    sep = ", ";
                }
                System.out.println("}");
                System.out.print("Live out: {");
                sep = "";
                Iterator aftIt = after.iterator();
                while(aftIt.hasNext()) {
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

    public static void main(String[] args) {
        // try {
        System.out.println("Hello World!");
        System.out.println(Options.v().soot_classpath());
        Options.v().set_soot_classpath("sootOutput");
        System.out.println(Options.v().soot_classpath());
        System.out.println("Hello World!");
        App app = new App();
            // app.genHelloWorld();
        app.runLiveAnalysis();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        // System.out.println("Hello World!");
    }
}
