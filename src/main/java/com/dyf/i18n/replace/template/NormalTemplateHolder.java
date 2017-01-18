package com.dyf.i18n.replace.template;

import com.dyf.i18n.excel.TableHolder;
import com.dyf.i18n.replace.NormalReplacer;
import com.dyf.i18n.replace.Replacer;
import com.dyf.i18n.util.ListStringUtil;
import com.dyf.i18n.util.escaper.Escaper;

import java.util.List;
import java.util.Map;

/**
 * Created by yuiff on 2017/1/18.
 */
public class NormalTemplateHolder implements TemplateHolder {
    private TableHolder tableHolder;
    private Escaper escaper;
    private String template;
    private String prefix;
    private String suffix;
    private Replacer replacer;

    public NormalTemplateHolder(TableHolder tableHolder, Escaper escaper, String template, String prefix, String suffix) {
        this(tableHolder, escaper, template, prefix, suffix, new NormalReplacer());
    }

    public NormalTemplateHolder(TableHolder tableHolder, Escaper escaper, String template) {
        this(tableHolder, escaper, template, "", "", new NormalReplacer());
    }

    public NormalTemplateHolder(TableHolder tableHolder, Escaper escaper, String template, String prefix, String suffix, Replacer replacer) {
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
        this.prefix = prefix;
    }

    @Override
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public String getRepacedTemplate(int colNum) {
        List<String> keyList = ListStringUtil.addPrefixSuffix(
                escaper.escape(
                        tableHolder.getColStringWithOutFirstRow(0)
                ), prefix, suffix);
        List<String> valueList = ListStringUtil.addPrefixSuffix(
                escaper.escape(
                        tableHolder.getColStringWithOutFirstRow(colNum)
                ), prefix, suffix);
        Map<String, String> kvMap = ListStringUtil.list2map(keyList, valueList);

        replacer.reset(kvMap);
        String outputString = replacer.doReplace(template);
        return outputString;
    }

    @Override
    public List<String> getFirstRowString() {
        return tableHolder.getFirstRowString();
    }

}
