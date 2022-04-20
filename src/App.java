import java.util.Random;
public class App {
    public static void main(String[] args) throws InterruptedException{
 
        

        // System.out.println("GETSIGNED1: " + longWord.getSigned());
        // System.out.println("GETSIGNED2: " + longWord2.getSigned());
        // System.out.println("GETUNSIGNED1: " + longWord.getUnsigned());
        // System.out.println("GETUNSIGNED2: " + longWord2.getUnsigned());


        // System.out.println("Longword1 " + longWord);
        // System.out.println("Longword2 " + longWord2);
        // System.out.println("1: shift left  " + longWord.ShiftLogicalLeft(2));
        // System.out.println("2: shift right " + longWord.ShiftLogicalRight(2));
        // System.out.println("3: shift Arith " + longWord.ShiftArithmeticRight(2));
        // System.out.println("4: not         " + longWord.not());
        // System.out.println("5: and         " + longWord.and(longWord2));
        // System.out.println("6: or          " + longWord.or(longWord2));
        // System.out.println("7: xor         " + longWord.xor(longWord2));

        //TO TEST, CREATE LONGWORD OBJECT AND USE .SET FOR A NUMBER.

        ALU alu = new ALU();
        
        
        
        for (int i = 0; i < 30; i++)
        {
            int j = i + 1;
            System.out.println("-------------------------------------TEST #" + j + "-------------------------------------");
            Random random1 = new Random();
            Random random2 = new Random();
            Random random3 = new Random();
            /*This whole block up until alu.rippleCarryAddTest
            is to test random numbers being added and subtracted*/
            LongWord longWord = new LongWord();
            LongWord longWord2 = new LongWord();
            //Change these numbers to change the range. If you want to include negative numbers (Max + min)-min
            //Shows you a number between 100000 and -100000
            //If you want to only include positive numbers then just replace the nextInt(max+min)-min with just nextInt(max)
            int rand = random1.nextInt(100000 + 100000) - 100000;
            int rand2= random2.nextInt(100000 + 100000) - 100000;
            int[] opcodes = {1000,1001,1010,1011,1100,1110,1111};
            int rand3 = random3.nextInt(7);
            longWord.set(rand);
            longWord2.set(rand2);
            System.out.println("");

            System.out.println("    "+alu.operate(opcodes[rand3], longWord, longWord2));
            


            System.out.println("Overflow flag true?: " + alu.getOverflowFlag());
            System.out.println("Negative flag true?: " + alu.getNegFlag());
            System.out.println("Carry flag true?: "+ alu.getOverflowFlag());
            System.out.println("Zero flag true?: "+ alu.getZeroFlag());
            System.out.println();
            System.out.println();
        }
    }
}