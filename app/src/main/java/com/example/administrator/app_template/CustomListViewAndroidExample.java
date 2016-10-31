package com.example.administrator.app_template;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by chau on 10/31/2016.
 */
public class CustomListViewAndroidExample extends Activity {

    ListView list;
    CustomAdapter adapter;
    public  CustomListViewAndroidExample CustomListView = null;
    public ArrayList<ListModel> CustomListViewValuesArr = new ArrayList<ListModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_list_view_android_example);

        CustomListView = this;

        /******** Take some data in Arraylist ( CustomListViewValuesArr ) ***********/
        setListData();

        Resources res =getResources();
        list= ( ListView )findViewById( R.id.list );  // List defined in XML ( See Below )

        /**************** Create Custom Adapter *********/
        adapter=new CustomAdapter( CustomListView, CustomListViewValuesArr,res );
        list.setAdapter( adapter );

    }

    /****** Function to set data in ArrayList *************/
    public void setListData()
    {

        for (int i = 0; i < 11; i++) {

            final ListModel sched = new ListModel();

            /******* Firstly take data in model object ******/
            sched.setCompanyName("Company "+i);
            sched.setImage("image"+i);
            sched.setUrl("http:\\www."+i+".com");

            /******** Take Model Object in ArrayList **********/
            CustomListViewValuesArr.add( sched );
        }

    }


    /*****************  This function used by adapter ****************/
    public void onItemClick(int mPosition)
    {
        ListModel tempValues = ( ListModel ) CustomListViewValuesArr.get(mPosition);


        // SHOW ALERT

        Toast.makeText(CustomListView,
                "" + tempValues.getCompanyName() + " Image:"+tempValues.getImage() +" Url:"+tempValues.getUrl(), Toast.LENGTH_LONG).show();
    }
}
