import java.util.BitSet;

public class LongWord {
    private BitSet Mainbit;

    public LongWord() {
        BitSet bitvec = new BitSet(32);
        bitvec.clear();
        Mainbit = bitvec;
    }

    @Override
    public String toString() {
        String bitString = "";
        String hexString = "";
        for (int i = 32; i != 0; i--) {
            if (i % 4 == 0) {
                // bitString += "\t";
            }
            if (this.Mainbit.get(i - 1) == true) {
                bitString += '1';
            } else {
                bitString += '0';
            }
        }
        for (int i = 32; i != 0; i -= 4) {
            int decimal = 0;
            for (int j = i - 4; j < i; j++) {
                if (this.Mainbit.get(j)) {
                    decimal = (int) (decimal + Math.pow(2, 4 - (i - j)));
                }
            }
            switch (decimal) {
                case 10:
                    hexString += "A";
                    break;
                case 11:
                    hexString += "B";
                    break;
                case 12:
                    hexString += "C";
                    break;
                case 13:
                    hexString += "D";
                    break;
                case 14:
                    hexString += "E";
                    break;
                case 15:
                    hexString += "F";
                    break;
                default:
                    hexString += decimal;
                    break;
            }
        }
        int value = 0;
        for (int i = 0; i < Mainbit.length(); ++i) {
            value += Mainbit.get(i) ? (1 << i) : 0;
        }
        return "Bits: " + bitString + " 0x" + hexString + " " + "Value: " + value;
    }

    public boolean getBit(int i) {
        return Mainbit.get(i);
    }

    public void setBit(int i) {
        Mainbit.set(i);
    }

    public void setBit(int i, boolean val) {
        Mainbit.set(i, val);
    }

    public void clearBit(int i) {
        Mainbit.set(i, false);
        ;
    }

    public void toggleBit(int i) {
        Mainbit.flip(i);
    }

    public int getSigned() {
        int value = 0;
        for (int i = 0; i < Mainbit.length(); ++i) {
            value += Mainbit.get(i) ? (1 << i) : 0;
        }
        return value;
    }

    public long getUnsigned() {
        long value = 0L;
        for (int i = 0; i < Mainbit.length(); ++i) {
            value += Mainbit.get(i) ? (1L << i) : 0L;
        }
        return value;
    }

    public void set(int value) { // for testing
        if (value < 0)// SETS A NEGATIVE VALUE
        {
            int index = 0;
            while (value != 0) {
                if (value % 2 != 0) {
                    Mainbit.set(index);
                }
                ++index;
                value = value >>> 1;
            }
            this.not();
        } else {
            int index = 0;
            while (value != 0) {
                if (value % 2 != 0) {
                    Mainbit.set(index);
                }
                ++index;
                value = value >>> 1;
            }
        }
    }

    public void copy(LongWord source) {
        for (int i = 0; i < 32; i++) {
            if (source.getBit(i) == true) {
                Mainbit.set(i, true);
            } else {
                Mainbit.set(i, false);
            }
        }
    }

    public LongWord ShiftLogicalLeft(int amount) {
        LongWord shiftLeft = new LongWord();
        shiftLeft.copy(this);
        for (int i = 0; i < amount; i++) {
            for (int j = 32; j != 0; j--) {
                shiftLeft.setBit(j, shiftLeft.getBit(j - 1));
            }
            shiftLeft.clearBit(0);
        }
        return shiftLeft;
    }

    public LongWord ShiftLogicalRight(int amount) {
        LongWord newLong = new LongWord();
        newLong.copy(this);
        for (int i = 0; i < amount; i++) {
            for (int j = 0; j != 31; j++) {
                newLong.setBit(j, newLong.getBit(j + 1));
            }
            newLong.clearBit(31);
        }
        return newLong;
    }

    public LongWord ShiftArithmeticRight(int amount) {
        LongWord newLong = new LongWord();
        newLong.copy(this);
        for (int i = 0; i < amount; i++) {
            for (int j = 0; j != 30; j++) {
                newLong.setBit(j, newLong.getBit(j + 1));
            }
            newLong.clearBit(30);
        }
        return newLong;
    }

    public LongWord not() {
        LongWord notLongWord = new LongWord();
        notLongWord.copy(this);
        for (int i = 0; i < 32; i++) {
            notLongWord.toggleBit(i);
        }
        return notLongWord;
    }

    public LongWord and(LongWord other) {
        LongWord andLongWord = new LongWord();
        andLongWord.copy(this);
        for (int i = 0; i < 32; i++) {
            if (andLongWord.getBit(i) && other.getBit(i)) {
                andLongWord.setBit(i);
            } else {
                andLongWord.clearBit(i);
            }
        }
        return andLongWord;
    }

    public LongWord or(LongWord other) {
        LongWord orLongWord = new LongWord();
        orLongWord.copy(this);
        for (int i = 0; i < 32; i++) {
            if (orLongWord.getBit(i) || other.getBit(i)) {
                orLongWord.setBit(i);
            } else {
                orLongWord.clearBit(i);
            }
        }
        return orLongWord;
    }

    public LongWord xor(LongWord other) {
        LongWord xorLongWord = new LongWord();
        xorLongWord.copy(this);
        for (int i = 0; i < 32; i++) {
            if (xorLongWord.getBit(i) != other.getBit(i)) {
                xorLongWord.setBit(i);
            } else {
                xorLongWord.clearBit(i);
            }
        }
        return xorLongWord;
    }

    public boolean isZero() {
        if (this.getSigned() == 0) {
            return true;
        } else {
            return false;
        }
    }
}