package com.feiyoung;

public enum DateEnum2 {
    ONE(1, "5348729601"),
    TWO(2, "7829463150"),
    THREE(3, "7951420386"),
    FOUR(4, "8706254139"),
    FIVE(5, "6235810479"),
    SIX(6, "0352481679"),
    SEVEN(7, "4128506973"),
    EIGHT(8, "7134089265"),
    NINE(9, "6908534172"),
    TEN(10, "4516893702"),
    ELEVEN(11, "1648092357"),
    TWELVE(12, "5072483169"),
    THIRTEEN(13, "6904283157"),
    FOURTEEN(14, "2930518647"),
    FIFTEEN(15, "7082436519"),
    SIXTEEN(16, "6318250947"),
    SEVENTEEN(17, "1079234568"),
    EIGHTEEN(18, "1498053672"),
    NINETEEN(19, "6852093471"),
    TWENTY(20, "8746395021"),
    TWENTYONE(21, "9283417605"),
    TWENTYTWO(22, "0583924176"),
    TWENTYTHREE(23, "9745216038"),
    TWENTYFOUR(24, "5174230896"),
    TWENTYFIVE(25, "7451628903"),
    TWENTYSIX(26, "5936820714"),
    TWENTYSEVEN(27, "4965287130"),
    TWENTYEIGHT(28, "5892176304"),
    TWENTYNINE(29, "8395604721"),
    THIRTY(30, "7528413960"),
    THIRTYONE(31, "9072846135");

    private int index;
    private String value;

    DateEnum2(int index, String value) {
        this.index = index;
        this.value = value;
    }
    public static String getKeyByIndex(int index) {
        for(DateEnum2 key: values()) {
            if (key.getIndex() == index) {
                return key.value;
            }
        }
        return null;
    }
    public int getIndex() {
        return this.index;
    }

}
