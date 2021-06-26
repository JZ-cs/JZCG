package Foundation;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

public class Calculation {
    public static final int SET_ALL_ZEROS = 0;
    public static final int SET_ALL_ONES = 1;
    public static final int SET_INCREASE = 2;
    public static final int SET_DOUBLE_MAX = 3;
    public static final int SET_DOUBLE_MIN = 4;
    public static final int SET_RANDOM_UINT16 = 5;
    public static final int SET_NONE = 6;

    public static final int ADD = 0;
    public static final int SUB = 1;
    public static final int MUL = 2;
    public static final int DIV = 3;
    public static final int MATMUL = 4;
    public static final int SUM = 5;
    public static final int MAX = 6;
    public static final int MIN = 7;
    public static final int TRANSPOSE = 8;

    public static final int EXP = 101;
    public static final int LOG = 102;
    public static final int SIN = 103;
    public static final int COS = 104;
    public static final int SIGMOID = 105;

    public static final int MSE_LOSS = 201;

    public static HashMap<Integer, String> opSign2String;


    public static boolean verifyOpSign(int opSign) {
        if(opSign2String == null){
            opSign2String = new HashMap<>();
            Field[] fields = Calculation.class.getDeclaredFields();
            for(Field field : fields){
                String descriptor = Modifier.toString(field.getModifiers());
                if(field.getType().equals(int.class) && descriptor.contains("final") && descriptor.contains("static")){
                    String fieldName = field.getName();
                    if(!fieldName.startsWith("SET")){
                        try{
                            Object value = field.get(Calculation.class);
                            opSign2String.put((Integer)value, fieldName);
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return opSign2String.containsKey(opSign);
    }
}




