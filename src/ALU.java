public class ALU {
    private boolean ZeroFlag;
    private boolean NegFlag;
    private boolean CarryFlag;
    private boolean OverflowFlag;

    public ALU() {
        ZeroFlag = false;
        NegFlag = false;
        CarryFlag = false;
        OverflowFlag = false;
    }

    public LongWord rippleCarryAddTest(LongWord a, LongWord b, String cin) {
        LongWord ret = new LongWord();
        boolean op = true;
        if (cin == "add") {
            op = true;
        }
        if (cin == "sub") {
            op = false;
        }
        ret = rippleCarryAdd(a, b, op);
        return ret;
    }

    private LongWord rippleCarryAdd(LongWord a, LongWord b, boolean cin) {
        if (cin) // ADDING
        {
            boolean outCarry = false;
            boolean inCarry = false;
            LongWord sum = new LongWord();
            for (int i = 0; i != 32; ++i) {
                inCarry = outCarry;
                outCarry = false;
                if (inCarry == false) {
                    if (a.getBit(i) == false && b.getBit(i) == false) {
                        sum.setBit(i, false);
                    }
                    if (a.getBit(i) != b.getBit(i)) {
                        sum.setBit(i, true);
                    }
                    if (a.getBit(i) == true && b.getBit(i) == true) {
                        sum.setBit(i, false);
                        outCarry = true;
                    }
                }
                if (inCarry == true) {
                    if (a.getBit(i) == false && b.getBit(i) == false) {
                        sum.setBit(i, true);
                    }
                    if (a.getBit(i) != b.getBit(i)) {
                        sum.setBit(i, false);
                        outCarry = true;
                    }
                    if (a.getBit(i) == true && b.getBit(i) == true) {
                        sum.setBit(i, true);
                        outCarry = true;
                    }
                }
            }
            if (outCarry == true) {
                setOverflowFlag(true);
            }
            System.out.println("a" + a + " " + a.getSigned());
            System.out.println("b" + b + " " + b.getSigned());
            System.out.println("Sum: " + sum);
            System.out.println("signed " + sum.getSigned());
            if (sum.getSigned() < 0) {
                setNegFlag(true);
            }
            return sum;
        } else if (!cin && a.getSigned() == b.getSigned())// If the two values are equal and you are subtracting
        {
            LongWord zero = new LongWord();
            zero.set(0);
            setZeroFlag(true);
            return zero;
        } else if (!cin) {
            boolean outCarry = false;
            boolean inCarry = false;
            LongWord difference = new LongWord();
            a = a.not();
            for (int i = 0; i != 32; i++) {
                inCarry = outCarry;
                outCarry = false;
                if (inCarry == false) {
                    if (a.getBit(i) == false && b.getBit(i) == false) {
                        difference.setBit(i, false);
                    }
                    if (a.getBit(i) != b.getBit(i)) {
                        difference.setBit(i, true);
                    }
                    if (a.getBit(i) == true && b.getBit(i) == true) {
                        difference.setBit(i, false);
                        outCarry = true;
                    }
                }
                if (inCarry == true) {
                    if (a.getBit(i) == false && b.getBit(i) == false) {
                        difference.setBit(i, true);
                    }
                    if (a.getBit(i) != b.getBit(i)) {
                        difference.setBit(i, false);
                        outCarry = true;
                    }
                    if (a.getBit(i) == true && b.getBit(i) == true) {
                        difference.setBit(i, true);
                        outCarry = true;
                    }
                }
            }
            System.out.println("a" + a.not() + " " + a.not().getSigned());
            System.out.println("b" + b + " " + b.getSigned());
            System.out.println("Difference:" + difference + " before not");
            difference = difference.not();
            System.out.println("Difference:" + difference + " after not");
            System.out.println("signed " + difference.getSigned());
            if (difference.getSigned() < 0) {
                setNegFlag(true);
            }
            return difference;
        } else {
            System.out.println("NEITHER TRUE");
            System.out.println("NEITHER TRUE");
            System.out.println("NEITHER TRUE");
            System.out.println("NEITHER TRUE");
            System.out.println("NEITHER TRUE");
            System.out.println("NEITHER TRUE");
            System.out.println("NEITHER TRUE");
            System.out.println("NEITHER TRUE");
            System.out.println("NEITHER TRUE");
            System.out.println("NEITHER TRUE");
            System.out.println("NEITHER TRUE");
            System.out.println("NEITHER TRUE");
            System.out.println("NEITHER TRUE");
            return a;
        }
    }

    public LongWord operate(int code, LongWord op1, LongWord op2) {
        switch (code) {
            case 1000:
                System.out.println("and");
                System.out.println("Op1:" + op1);
                System.out.println("Op2:" + op2);
                return and(op1, op2);
            case 1001:
                System.out.println("or");
                System.out.println("Op1:" + op1);
                System.out.println("Op2:" + op2);
                return or(op1, op2);
            case 1010:
                System.out.println("xor");
                System.out.println("Op1:" + op1);
                System.out.println("Op2:" + op2);
                return xor(op1, op2);
            // case 1010: cant figure this one out
            case 1011:
                System.out.println("add");
                return rippleCarryAdd(op1, op2, true);
            case 1100:
                System.out.println("subtract");
                return rippleCarryAdd(op1, op2, false);
            case 1101:
                System.out.println("SLL");
                System.out.println("Op1:" + op1);
                System.out.println("Op2: " + op2.getUnsigned() % 32);
                return ShiftLogicalLeft(op1, (int) op2.getUnsigned() % 32);
            case 1110:
                System.out.println("SLR");
                System.out.println("Op1:" + op1);
                System.out.println("Op2: " + op2.getUnsigned() % 32);
                return ShiftLogicalRight(op1, (int) op2.getUnsigned() % 32);
            case 1111:
                System.out.println("SAR");
                System.out.println("Op1:" + op1);
                System.out.println("Op2: " + op2.getUnsigned() % 32);
                return ShiftArithmeticRight(op1, (int) op2.getUnsigned() % 32);
            default:
                LongWord bad = new LongWord();
                bad.set(0);
                return bad;
        }
    }

    private LongWord or(LongWord A, LongWord B) {
        return A.or(B);
    }

    private LongWord and(LongWord A, LongWord B) {
        return A.and(B);
    }

    private LongWord xor(LongWord A, LongWord B) {
        return A.xor(B);
    }

    private LongWord ShiftLogicalLeft(LongWord A, int amount) {
        return A.ShiftLogicalLeft(amount);
    }

    private LongWord ShiftLogicalRight(LongWord A, int amount) {
        return A.ShiftLogicalRight(amount);
    }

    private LongWord ShiftArithmeticRight(LongWord A, int amount) {
        return A.ShiftArithmeticRight(amount);
    }

    /**
     * @return the zeroFlag
     */
    public boolean getZeroFlag() {
        return ZeroFlag;
    }

    /**
     * @param zeroFlag the zeroFlag to set
     */
    private void setZeroFlag(boolean zeroFlag) {
        ZeroFlag = zeroFlag;
    }

    /**
     * @return the negFlag
     */
    public boolean getNegFlag() {
        return NegFlag;
    }

    /**
     * @param negFlag the negFlag to set
     */
    private void setNegFlag(boolean negFlag) {
        NegFlag = negFlag;
    }

    /**
     * @return the carryFlag
     */
    public boolean getCarryFlag() {
        return CarryFlag;
    }

    /**
     * @param carryFlag the carryFlag to set
     */
    public void setCarryFlag(boolean carryFlag) {
        CarryFlag = carryFlag;
    }

    /**
     * @return the overflowFlag
     */
    public boolean getOverflowFlag() {
        return OverflowFlag;
    }

    /**
     * @param overflowFlag the overflowFlag to set
     */
    private void setOverflowFlag(boolean overflowFlag) {
        OverflowFlag = overflowFlag;
    }
}