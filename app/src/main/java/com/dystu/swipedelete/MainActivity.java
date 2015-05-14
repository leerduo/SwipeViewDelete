package com.dystu.swipedelete;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements SwipeView.OnSwipeStatusChangeListener {

    private ArrayList<MyBean> list = new ArrayList<>();

    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list);
        for (int i = 0; i < 30; i++) {
            list.add(new MyBean("item " + i));
        }
        listView.setAdapter(new MyAdapter());


       /* listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               // Toast.makeText(MainActivity.this,"第" + position + "个",Toast.LENGTH_SHORT).show();
            }
        });*/


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    if (unClosedSwipeView.size() > 0) {
                        closeAllOpenedSwipeView();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }


    private ArrayList<SwipeView> unClosedSwipeView = new ArrayList<>();

    @Override
    public void onOpen(SwipeView openedSwipeView) {

        for (int i = 0; i < unClosedSwipeView.size(); i++) {
            if (unClosedSwipeView.get(i) != openedSwipeView) {
                unClosedSwipeView.get(i).close();
            }
        }
        if (!unClosedSwipeView.contains(openedSwipeView)) {
            unClosedSwipeView.add(openedSwipeView);
        }
    }

    @Override
    public void onClose(SwipeView closedSwipeView) {
        unClosedSwipeView.remove(closedSwipeView);
    }

    @Override
    public void onSwiping(SwipeView swipingSwipeView) {
        if (!unClosedSwipeView.contains(swipingSwipeView)) {
            closeAllOpenedSwipeView();
        }
        unClosedSwipeView.add(swipingSwipeView);

    }


    private void closeAllOpenedSwipeView() {
        for (int i = 0; i < unClosedSwipeView.size(); i++) {
            if (unClosedSwipeView.get(i).getSwipeStatus() != SwipeView.SwipeStatus.Close) {
                unClosedSwipeView.get(i).close();
            }
        }
    }


    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        ViewHolder holder;

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(MainActivity.this, R.layout.item, null);
            }
            holder = getViewHolder(convertView);

            holder.swipeView.setOnSwipeStatusChangeListener(MainActivity.this);

            holder.swipeView.fastClose();

            MyBean myBean = list.get(position);
            holder.content.setText(myBean.getNum());
            holder.delete.setText("delete");

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.remove(position);
                    notifyDataSetChanged();
                }
            });


            holder.content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (unClosedSwipeView.size() > 0) {
                        closeAllOpenedSwipeView();
                    } else {
                        Toast.makeText(MainActivity.this, "第" + position + "个", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return convertView;
        }


        public ViewHolder getViewHolder(View convertView) {
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            if (viewHolder == null) {
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }
            return viewHolder;
        }

        class ViewHolder {
            SwipeView swipeView;
            TextView content;
            TextView delete;

            public ViewHolder(View convertView) {
                content = (TextView) convertView.findViewById(R.id.content);
                delete = (TextView) convertView.findViewById(R.id.delete);
                swipeView = (SwipeView) convertView.findViewById(R.id.swipeView);
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
