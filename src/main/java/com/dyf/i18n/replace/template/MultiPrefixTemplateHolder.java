package com.dyf.i18n.replace.template;

import com.dyf.i18n.replace.NormalReplacer;
import com.dyf.i18n.replace.Replacer;
import com.dyf.i18n.table.TableHolder;
import com.dyf.i18n.util.ListStringUtil;
import com.dyf.i18n.util.escaper.Escaper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yuiff on 2017/1/18.
 */
public class MultiPrefixTemplateHolder implements TemplateHolder {
    private TableHolder tableHolder;
    private Escaper escaper;
    private String template;
    private String[] prefix;
    private String[] suffix;
    private Replacer replacer;

    public MultiPrefixTemplateHolder(TableHolder tableHolder, Escaper escaper, String template, String[] prefix, String[] suffix) {
        this(tableHolder, escaper, template, prefix, suffix, new NormalReplacer());
    }

    public MultiPrefixTemplateHolder(TableHolder tableHolder, Escaper escaper, String template) {
        this(tableHolder, escaper, template, null, null, new NormalReplacer());
    }

    public MultiPrefixTemplateHolder(TableHolder tableHolder, Escaper escaper, String template, String[] prefix, String[] suffix, Replacer replacer) {
        this.tableHolder = tableHolder;
        this.escaper = escaper;
        this.template = template;
        this.prefix = prefix;
        this.suffix = suffix;
        this.replacer = replacer;
    }

    @Override
    public void setTableHolder(TableHolder tableHolder) {
        this.tableHolder = tableHolder;
    }

    @Override
    public void setEscaper(Escaper escaper) {
        this.escaper = escaper;
    }

    @Override
    public void setTemplate(String template) {
        this.template = template;
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = new String[]{prefix};
    }

    @Override
    public void setSuffix(String suffix) {
        this.suffix = new String[]{suffix};
    }

    public void setPrefixSuffix(String[] prefix, String[] suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public String getRepacedTemplate(int colNum) {
        List<String> keyList = new ArrayList<>();
        List<String> valueList = new ArrayList<>();
        for (int i = 0; i < prefix.length; i++) {
            List<String> newKeyList = ListStringUtil.addPrefixSuffix(
                    escaper.escape(
                            tableHolder.getColStringWithOutFirstRow(0)
                    ), prefix[i], suffix[i]);
            List<String> newValueList = ListStringUtil.addPrefixSuffix(
                    escaper.escape(
                            tableHolder.getColStringWithOutFirstRow(colNum)
                    ), prefix[i], suffix[i]);
            keyList.addAll(newKeyList);
            valueList.addAll(newValueList);
        }
        Map<String, String> kvMap = ListStringUtil.list2mapPreferDifferent(keyList, valueList, prefix, suffix);
//        System.out.println(kvMap);
        replacer.reset(kvMap);
        String outputString = replacer.doReplace(template);
        return outputString;
    }

    @Override
    public List<String> getFirstRowString() {
        return tableHolder.getFirstRowString();
    }

}
