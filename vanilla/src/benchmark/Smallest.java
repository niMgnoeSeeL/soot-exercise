package benchmark;

class IntObj {
    public int value;

    public IntObj() {
    }

    public IntObj(int i) {
        value = i;
    }
}

class FloatObj {
    public float value;

    public FloatObj() {
    }

    public FloatObj(float i) {
        value = i;
    }
}

class LongObj {
    public long value;

    public LongObj() {
    }

    public LongObj(long i) {
        value = i;
    }
}

class DoubleObj {
    public double value;

    public DoubleObj() {
    }

    public DoubleObj(double i) {
        value = i;
    }
}

class CharObj {
    public char value;

    public CharObj() {
    }

    public CharObj(char i) {
        value = i;
    }
}

public class Smallest {
    public java.util.Scanner scanner;
    public String output = "";

    public static void main(String[] args) throws Exception {

        Smallest mainClass = new Smallest();
        if (args.length > 0) {
            mainClass.scanner = new java.util.Scanner(args[0]);
        } else {
            mainClass.scanner = new java.util.Scanner(System.in);
        }
        mainClass.output = mainClass.exec();
        System.out.println(mainClass.output);
    }

    public String exec() throws Exception {
        IntObj frst = new IntObj(), scnd = new IntObj(), thrd = new IntObj(), frth = new IntObj();
        output += (String.format("Please enter 4 numbers separated by spaces > "));
        frst.value = scanner.nextInt();
        scnd.value = scanner.nextInt();
        thrd.value = scanner.nextInt();
        frth.value = scanner.nextInt();

        return Smallest.compare(frst.value, scnd.value, thrd.value, frth.value);
    }

    public static String compare(int frst, int scnd, int thrd, int frth) {
        String output = "";
        if (frst < scnd && frst < thrd
                && frst < frth) {
            output += (String.format("%d is the smallest\n", frst));
        } else if (scnd < frst && scnd < thrd
                && scnd < frth) {
            output += (String.format("%d is the smallest\n", scnd));
        } else if (thrd < frst && thrd < scnd
                && thrd < frth) {
            output += (String.format("%d is the smallest\n", thrd));
        } else {
            output += (String.format("%d is the smallest\n", frth));
        }
        if (true)
            return output;
        else
            return "";
    }
}