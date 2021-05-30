package converter;

import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
//Converter<原始类型，目标类型>
public class MyDateConverter implements Converter<String, Date> {
    private String pattern;

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    /**
     * 核心的作用就是
     *      String ---> Date
     *      将我们配置文件中的String类型转换成我们要的Date类型
     * 那么问题来了:
     * 1.我们怎么才能从配置文件中获得我们的日期格式的字符串呢？
     * 答：这个@param s，就是配置文件中的日期格式的字符串了，Spring框架会为我们提供的
     *
     * 2.那么我们怎么才能将根据日期格式的字符串用我们的代码转换好的日期类型，填到对应Bean的属性值中呢？
     * 答：这个Spring也帮我们处理好了，我们将转换好的日期类型，作为返回值进行返回，那么Spring就会自动的进行属性值得注入操作
     *      其实呢就是，接口方法的回调！
     * @param s
     * @return
     */
    @Override
    public Date convert(String s) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = sdf.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}
