package com.example.hongjeongmin.memo;

import android.provider.BaseColumns;

public final class MemoContract {
    private MemoContract(){

    }//인스턴스화 금지

    //테이블 정보를 내부 클래스로 정의하기 (테이블 명, 칼럼 이름+ baseColumns에서 기본으로 생성해주는 id, count)
    public static class memoEntry implements BaseColumns{
        public static final String TABLE_NAME="memo"; //Table이름
        public static final String COLUMN_NAME_TITLE="title";
        public static final String COLUMN_NAME_CONTENTS="contents";
    }

    //테이블을 더 정의하고 싶으면 여기에 위와같이 내부클래스로 정의해주면 된다.
}
