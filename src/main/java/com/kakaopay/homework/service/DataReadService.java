package com.kakaopay.homework.service;

import com.kakaopay.homework.exception.server.DataReadWriteFailException;

public interface DataReadService {
    void readAndStoreData(String path, String charset) throws DataReadWriteFailException;
}
