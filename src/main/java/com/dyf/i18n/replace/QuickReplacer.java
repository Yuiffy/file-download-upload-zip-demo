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
public class QuickReplacer implements Replacer {

    private Map<String, String> tokens;
    private Pattern pattern;

    public QuickReplacer() {
        this.reset(new HashMap<String, String>());
    }

    public QuickReplacer(Map<String, String> mp) {
        this.reset(mp);
    }

    @Override
    public String put(String key, String value) {
        String ret = tokens.put(key, value);
        updatePattern();
        return ret;
    }

    @Override
    public void reset(Map<String, String> kvMap) {
        tokens = kvMap;
        updatePattern();
    }

    private void updatePattern() {
        List<String> keyList = new ArrayList<>();
        //do regex escape for value string, because will make keys into patternString
        for (String key : tokens.keySet())
            keyList.add(Pattern.quote(key));
        String patternString = "(" + StringUtils.join(keyList, "|") + ")";
        pattern = Pattern.compile(patternString);
    }

    @Override
    public String doReplace(String template) {
        Matcher matcher = pattern.matcher(template);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, Matcher.quoteReplacement(tokens.get(matcher.group(1))));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
