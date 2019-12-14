package trade.example.application.tradebrains;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    MaterialSearchView searchview;
    FirebaseDatabase database;
    DatabaseReference mDatabaseReference;
    ArrayList<String> list;
    TinyDB tinyDB=null;
    ListView mRecyclerView;
    ArrayAdapter<String> adapter=null;
    private ProgressDialog progressDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        android.support.v7.widget.Toolbar mToolbar=findViewById(R.id.toolbar);

        searchview=findViewById(R.id.search_view);
        mRecyclerView=findViewById(R.id.recycler_view);
        database = FirebaseDatabase.getInstance();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Search");
        mToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        mDatabaseReference=database.getReference().child("Company");
        list=new ArrayList<String>();
         tinyDB=new TinyDB(this);
        progressDialog = new ProgressDialog(this);

        progressDialog.setTitle("Make sure you are online");

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();

                for (DataSnapshot ds:dataSnapshot.getChildren())
                { //Log.i("ans",ds.getKey()+" "+ds.getValue());

                            Log.i("ans",ds.getKey()+" "+ds.getValue());

                            list.add(ds.getKey());

                }
                tinyDB.putListString("country",list);
                 adapter=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1
                        ,list );
                mRecyclerView.setAdapter(adapter);

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        searchview.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                Log.i("text changed","True");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    list.clear();
                    if (newText!=null&&!newText.isEmpty())
                    {
                        for (String s:tinyDB.getListString("country"))
                        {
                            if (s.contains(newText))
                            {
                                Log.i("Country searched for:",s+" hai");

                                list.add(s);
                            }

                        }
                    }
                    else
                    {

                        list.addAll(tinyDB.getListString("country"));

                    }
                    adapter=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1
                            ,list );
                    mRecyclerView.setAdapter(adapter);
                }
                return false;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item,menu);
        MenuItem menuItem=menu.findItem(R.id.action_search);
        searchview.setMenuItem(menuItem);
        return true;
    }
}
