package com.example.hongjeongmin.memo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_INSERT = 1000;
    private MemoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, MemoActivity.class), REQUEST_CODE_INSERT);
            }
        });

        ListView listView = (ListView) findViewById(R.id.memo_list);
        MemoDbHelper dbHelper = MemoDbHelper.getInstance(this);

        //db에 들어있는 값 조회는 커서로 한다.
        Cursor cursor = dbHelper.getReadableDatabase().query(MemoContract.memoEntry.TABLE_NAME, null
                , null, null, null, null, null);
        //id에 대하여 내림차순 정렬, 최근에 저장한게 가장 위로 간다.

        mAdapter=new MemoAdapter(this,cursor);
        listView.setAdapter(mAdapter);

        //메모 삭제 롱클릭 이벤트
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {

                final long deleteId=id;
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("메모 삭제");
                builder.setMessage("메모를 삭제하시겠습니까?");
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SQLiteDatabase db=MemoDbHelper.getInstance(MainActivity.this).getWritableDatabase();
                        int deleteCount=db.delete(MemoContract.memoEntry.TABLE_NAME,MemoContract.memoEntry._ID+"="+deleteId,null);
                        if(deleteCount==0){
                            Toast.makeText(MainActivity.this,"삭제에 문제가 발생하였습니다.",Toast.LENGTH_SHORT).show();
                        }else{
                            mAdapter.swapCursor(getMemoCursor());
                            Toast.makeText(MainActivity.this,"메모가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("취소",null);
                builder.show();
                return true;
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent=new Intent(MainActivity.this,MemoActivity.class);
                Cursor cursor=(Cursor)mAdapter.getItem(position);
                String title=cursor.getString(cursor.getColumnIndexOrThrow(MemoContract.memoEntry.COLUMN_NAME_TITLE));
                String contents=cursor.getString(cursor.getColumnIndexOrThrow(MemoContract.memoEntry.COLUMN_NAME_CONTENTS));

                intent.putExtra("id",id);
                intent.putExtra("title",title);
                intent.putExtra("contents",contents);
                startActivityForResult(intent,REQUEST_CODE_INSERT);
            }
        });
    }

    //DB에서 가져오는 부분의 중복을 피하기 위해서 커서를 가져오는 메소드를 만듬
    private Cursor getMemoCursor(){
        MemoDbHelper dbHelper=MemoDbHelper.getInstance(this);
        return dbHelper.getReadableDatabase().query(MemoContract.memoEntry.TABLE_NAME, null
                , null, null, null, null, MemoContract.memoEntry._ID+" DESC");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_INSERT&&resultCode==RESULT_OK){
            mAdapter.swapCursor(getMemoCursor()); //어댑터에 데이터가 변경되기를 알려주기윟해서 갱신된 커서를 swapcursor에 전달.
        }
    }

    public static class MemoAdapter extends CursorAdapter {

        public MemoAdapter(Context context, Cursor c) {
            super(context, c, false);
        }

        //리스트에 각 아이템에 해당할 레이아웃은 안드에서 기본 제공하는 텍스트뷰 하나만 포함하는 simple_list_item1으로 함.
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        //리스트뷰 아이템 실제로 표현
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView titleText = (TextView) view.findViewById(android.R.id.text1);
            titleText.setText(cursor.getString(cursor.getColumnIndexOrThrow(MemoContract.memoEntry.COLUMN_NAME_TITLE)));
        }
    }


}
