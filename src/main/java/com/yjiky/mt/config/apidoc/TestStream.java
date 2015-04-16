package com.yjiky.mt.config.apidoc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyance-dev-01 on 03/04/2015.
 */
public class TestStream {

    public static void main (String args[]) {
        List<TestA> testAs = new ArrayList<>();
        List<TestB> testBList = new ArrayList<>();
        testAs.stream().forEach(testA -> testBList.add(testA.testB));


    }

    class TestA {
        protected TestB testB = new TestB();
    }

    class TestB {

    }
}
