package com.sec.myonlinefm.xmlcheck;

import java.io.InputStream;
import java.util.List;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/3.
 */

public interface LocalInfoParser {
    public List<LocalInfo> parse(InputStream is) throws Exception;
    public String serialize(List<LocalInfo> books) throws Exception;
}
