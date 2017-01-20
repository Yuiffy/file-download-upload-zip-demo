package com.dyf.i18n.replace.template;

import com.dyf.i18n.table.TableHolder;
import com.dyf.i18n.util.escaper.Escaper;

import java.util.List;

/**
 * Created by yuiff on 2017/1/18.
 */
public interface TemplateHolder {
    void setTableHolder(TableHolder tableHolder);

    void setEscaper(Escaper escaper);

    void setTemplate(String template);

    void setPrefix(String prefix);

    void setSuffix(String suffix);

    String getRepacedTemplate(int colNum);

    List<String> getFirstRowString();
}
