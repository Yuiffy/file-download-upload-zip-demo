package com.dyf.i18n.replace;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yuiff on 2017/1/3.
 */
public class NormalReplacer implements Replacer {

    private Map<String, String> tokens;

    public NormalReplacer() {
        tokens = new HashMap<>();
    }

    public NormalReplacer(Map<String, String> mp) {
        tokens = mp;
    }

    @Override
    public String put(String key, String value) {
        return tokens.put(key, value);
    }

    @Override
    public void reset(Map<String, String> kvMap) {
        tokens = kvMap;
    }

    @Override
    public String doReplace(String template) {
        List<String> keyList = new ArrayList<>();
        //do regex escape for value string, because will make keys into patternString
        for (String key : tokens.keySet())
            keyList.add(Pattern.quote(key));
        String patternString = "(" + StringUtils.join(keyList,"|") + ")";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(template);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, Matcher.quoteReplacement(tokens.get(matcher.group(1))));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
