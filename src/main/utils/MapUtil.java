package main.utils;

import java.math.BigDecimal;
import java.util.HashMap;

public class MapUtil extends HashMap<String, Object> {

    private static final long serialVersionUID = 1992072100000000000L;

    @Override
    public MapUtil put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    /**
     * @param key 键
     */
    public Boolean getBoolean(String key){
        return getBoolean(key,false,false);
    }

    /**
     * @param key 键
     * @param isNull 是否保留null
     */
    public Boolean getBoolean(String key,Boolean isNull){
        return getBoolean(key,isNull,false);
    }

    /**
     * @param key 键
     * @param isNull 是否保留null
     * @param isRemove 是否移除
     */
    public Boolean getBoolean(String key,Boolean isNull,Boolean isRemove){
        Object obj = this.get(key);
        if(isRemove && null != obj){
            this.remove(key);
        }
        if(null != obj){
            return Boolean.valueOf(obj.toString());
        }
        if(isNull){
            return null;
        }else{
            return false;
        }
    }

    /**
     * @param key 键
     */
    public String getString(String key){
        return getString(key,false,false);
    }

    /**
     * @param key 键
     * @param isNull 是否保留null
     */
    public String getString(String key,Boolean isNull){
        return getString(key,isNull,false);
    }

    /**
     * @param key 键
     * @param isNull 是否保留null
     * @param isRemove 是否移除
     */
    public String getString(String key,Boolean isNull,Boolean isRemove){
        Object obj = this.get(key);
        if(isRemove && null != obj){
            this.remove(key);
        }
        if(null != obj){
            return obj.toString();
        }
        if(isNull){
            return null;
        }else{
            return "";
        }
    }

    /**
     * @param key 键
     */
    public Integer getInteger(String key){
        return getInteger(key,false,false);
    }

    /**
     * @param key 键
     * @param isNull 是否保留null
     */
    public Integer getInteger(String key,Boolean isNull){
        return getInteger(key,isNull,false);
    }

    /**
     * @param key 键
     * @param isNull 是否保留null
     * @param isRemove 是否移除
     */
    public Integer getInteger(String key,Boolean isNull,Boolean isRemove){
        Object obj = this.get(key);
        if(isRemove && null != obj){
            this.remove(key);
        }
        if(null != obj && isNumber(obj)){
            return Integer.valueOf(obj.toString());
        }
        if(isNull){
            return null;
        }else{
            return 0;
        }
    }

    /**
     * @param key 键
     */
    public Double getDouble(String key){
        return getDouble(key,false,false);
    }

    /**
     * @param key 键
     * @param isNull 是否保留null
     */
    public Double getDouble(String key,Boolean isNull){
        return getDouble(key,isNull,false);
    }

    /**
     * @param key 键
     * @param isNull 是否保留null
     * @param isRemove 是否移除
     */
    public Double getDouble(String key,Boolean isNull,Boolean isRemove){
        Object obj = this.get(key);
        if(isRemove && null != obj){
            this.remove(key);
        }
        if(null != obj && isNumber(obj)){
            return Double.valueOf(obj.toString());
        }
        if(isNull){
            return null;
        }else{
            return 0.0;
        }
    }

    /**
     * @param key 键
     */
    public Long getLong(String key){
        return getLong(key,false,false);
    }

    /**
     * @param key 键
     * @param isNull 是否保留null
     */
    public Long getLong(String key,Boolean isNull){
        return getLong(key,isNull,false);
    }

    /**
     * @param key 键
     * @param isNull 是否保留null
     * @param isRemove 是否移除
     */
    public Long getLong(String key,Boolean isNull,Boolean isRemove){
        Object obj = this.get(key);
        if(isRemove && null != obj){
            this.remove(key);
        }
        if(null != obj && isNumber(obj)){
            return Long.valueOf(obj.toString());
        }
        if(isNull){
            return null;
        }else{
            return 0L;
        }
    }

    /**
     * @param key 键
     */
    public BigDecimal getBigDecimal(String key){
        return getBigDecimal(key,false,false);
    }

    /**
     * @param key 键
     * @param isNull 是否保留null
     */
    public BigDecimal getBigDecimal(String key,Boolean isNull){
        return getBigDecimal(key,isNull,false);
    }

    /**
     * @param key 键
     * @param isNull 是否保留null
     * @param isRemove 是否移除
     */
    public BigDecimal getBigDecimal(String key,Boolean isNull,Boolean isRemove){
        Object obj = this.get(key);
        if(isRemove && null != obj){
            this.remove(key);
        }
        if(null != obj && isNumber(obj)){
            return BigDecimal.valueOf(Double.valueOf(obj.toString()));
        }
        if(isNull){
            return null;
        }else{
            return new BigDecimal(0);
        }
    }

    //{Boolean类型判断模块开始}

    /**
     * 判断是否为数字
     */
    public static Boolean isNumber(Object obj){
        if(null == obj){
            return false;
        }else{
            String str = obj.toString();
            return str.matches("-?[0-9]+.*[0-9]*");
        }
    }

    /**
     * 判断是否为null或者""
     */
    public static Boolean isBlank(Object str){
        if(null == str){
            return true;
        }else{
            if("".equals(str.toString().trim())){
             return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为数字
     */
    public static boolean isName(String name) {
        String regex = "^[a-z0-9A-Z\u4e00-\u9fa5_/-]{1,16}+$";
        return name.matches(regex);
    }

    //{Boolean类型判断模块结束}
}
