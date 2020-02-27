package com.feiyoung;

public enum DateEnum {
    ONE(1, "5084972163"),
    TWO(2, "9801567243"),
    THREE(3, "7286059143"),
    FOUR(4, "1850394726"),
    FIVE(5, "1462578093"),
    SIX(6, "5042936178"),
    SEVEN(7, "0145937682"),
    EIGHT(8, "0964238571"),
    NINE(9, "3497651802"),
    TEN(10, "9125780643"),
    ELEVEN(11, "8634972150"),
    TWELVE(12, "5924673801"),
    THIRTEEN(13, "8274053169"),
    FOURTEEN(14, "5841792063"),
    FIFTEEN(15, "2469385701"),
    SIXTEEN(16, "8205349671"),
    SEVENTEEN(17, "7429516038"),
    EIGHTEEN(18, "3769458021"),
    NINETEEN(19, "5862370914"),
    TWENTY(20, "8529364170"),
    TWENTYONE(21, "7936082154"),
    TWENTYTWO(22, "5786241930"),
    TWENTYTHREE(23, "0728643951"),
    TWENTYFOUR(24, "9418360257"),
    TWENTYFIVE(25, "5093287146"),
    TWENTYSIX(26, "5647830192"),
    TWENTYSEVEN(27, "3986145207"),
    TWENTYEIGHT(28, "0942587136"),
    TWENTYNINE(29, "4357069128"),
    THIRTY(30, "0956723814"),
    THIRTYONE(31, "1502796384");

    private int index;
    private String value;

    DateEnum(int index, String value) {
        this.index = index;
        this.value = value;
    }
    public static String getKeyByIndex(int index) {
        for(DateEnum key: values()) {
            if (key.getIndex() == index) {
                return key.value;
            }
        }
        return null;
    }
    public int getIndex() {
        return this.index;
    }

//    public static DateEnum getDateEnumByIndex(int var0) {
//        DateEnum[] var1 = values();
//        int var2 = var1.length;
//        for(int var3 = 0; var3 < var2; ++var3) {
//            DateEnum var4 = var1[var3];
//            if (var4.getIndex() == var0) {
//                return var4;
//            }
//        }
//        return null;
//    }
//    public String getKey() {
//        return this.key;
//    }
//    public void setIndex(int var1) {
//        this.index = var1;
//    }
//    public void setKey(String var1) {
//        this.key = var1;
//    }
}
