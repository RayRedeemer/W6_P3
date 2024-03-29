package com.example.sse.customlistview_sse;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.Rating;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

//Step-By-Step, Setting up the ListView

    private
    ListView lvEpisodes;     //Reference to the listview GUI component
    ListAdapter lvAdapter;   //Reference to the Adapter used to populate the listview.
    ListAdapter sbtAdapter;  //adapter for sorting by title
    ListAdapter sbvAdapter;  //adapter for sorting by rating

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences("rating", Context.MODE_PRIVATE);
        lvEpisodes = (ListView)findViewById(R.id.lvEpisodes);
        lvAdapter = new MyCustomAdapter(this.getBaseContext(), sp);  //instead of passing the boring default string adapter, let's pass our own, see class MyCustomAdapter below!
        lvEpisodes.setAdapter(lvAdapter);
        sbtAdapter = new SortByTitle(this.getBaseContext(),sp);
        sbvAdapter = new SortByValue(this.getBaseContext(),sp);

        lvEpisodes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String title = (String) lvEpisodes.getItemAtPosition(i);
                Log.i("title:", title);
                String url;
                if (title.compareTo("Spock's Brain") == 0) {
                    url = "http://www.imdb.com/title/tt0708449/";
                } else if (title.compareTo("Arena") == 0) {
                    url = "http://www.imdb.com/title/tt0708418/";
                } else if (title.compareTo("This Side of Paradise") == 0) {
                    url = "http://www.imdb.com/title/tt0708483/";
                } else if (title.compareTo("Mirror, Mirror") == 0) {
                    url = "http://www.imdb.com/title/tt0708438/";
                } else if (title.compareTo("Plato's Step Children") == 0) {
                    url = "http://www.imdb.com/title/tt0708443/";
                } else if (title.compareTo("The Naked Time") == 0) {
                    url = "http://www.imdb.com/title/tt0708473/";
                } else {
                    url = "http://www.imdb.com/title/tt0708480/";
                }

                Uri webpage = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        sp = getSharedPreferences("rating", Context.MODE_PRIVATE);
        lvAdapter = new MyCustomAdapter(this.getBaseContext(), sp);
        super.onResume();
    }

    @Override
    protected void onPause() {
        sp = ((MyCustomAdapter)lvAdapter).getSp();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);   //get rid of default behavior.

        // Inflate the menu; this adds items to the action bar
        getMenuInflater().inflate(R.menu.my_test_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.mnu_zero) {
            Toast.makeText(getBaseContext(), "Menu Zero.", Toast.LENGTH_LONG).show();
            return true;
        }

        if (id == R.id.mnu_one) {
            Toast.makeText(getBaseContext(), "Ring ring, Hi Mom.", Toast.LENGTH_LONG).show();
            return true;
        }

        if (id == R.id.mnu_two) {
//            ((MyCustomAdapter) lvAdapter).sortByTitle();
//            ((MyCustomAdapter) lvAdapter).notifyDataSetChanged();
            lvEpisodes.setAdapter(sbtAdapter);
            return true;
        }

        if (id == R.id.mnu_three) {
//            Toast.makeText(getBaseContext(), "Hangup it's a telemarketer.", Toast.LENGTH_LONG).show();
//            ((MyCustomAdapter) lvAdapter).sortByRating();
//            ((MyCustomAdapter) lvAdapter).notifyDataSetChanged();
            lvEpisodes.setAdapter(sbvAdapter);
            return true;
        }

        if (id == R.id.mnu_four) {
            MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.live_long_and_prosper);
            mp.start();
            return true;
        }

        if (id == R.id.mnu_five) {
            Intent intent = new Intent(this, VideoPlayer.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);  //if none of the above are true, do the default and return a boolean.
    }
}


//***************************************************************//
//create a  class that extends BaseAdapter
//Hit Alt-Ins to easily implement required BaseAdapter methods.
//***************************************************************//
//
//class m2 extends BaseAdapter{
//    @Override
//    public int getCount() {
//        return 0;
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return null;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return 0;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        return null;
//    }
//}

//STEP 1: Create references to needed resources for the ListView Object.  String Arrays, Images, etc.

class MyCustomAdapter extends BaseAdapter{

    SharedPreferences sp;

    private
    String episodes[];             //Keeping it simple.  Using Parallel arrays is the introductory way to store the List data.
    String  episodeDescriptions[];  //the "better" way is to encapsulate the list items into an object, then create an arraylist of objects.
    //     int episodeImages[];         //this approach is fine for now.
    ArrayList<Integer> episodeImages;  //Well, we can use one arrayList too...  Just mixing it up, Arrays or Templated ArrayLists, you choose.

//    ArrayList<String> episodes;
//    ArrayList<String> episodeDescriptions;

    RatingBar rbEpisode;
    Button btnRandom;
    Context context;   //Creating a reference to our context object, so we only have to get it once.  Context enables access to application specific resources.
    // Eg, spawning & receiving intents, locating the various managers.

    public SharedPreferences getSp(){
        return sp;
    }


    //STEP 2: Override the Constructor, be sure to:
    // grab the context, we will need it later, the callback gets it as a parm.
    // load the strings and images into object references.
    public MyCustomAdapter(Context aContext, SharedPreferences sp) {

//initializing our data in the constructor.
        context = aContext;  //saving the context we'll need it again.

        episodes = aContext.getResources().getStringArray(R.array.episodes);  //retrieving list of episodes predefined in strings-array "episodes" in strings.xml
        episodeDescriptions = aContext.getResources().getStringArray(R.array.episode_descriptions);

//This is how you would do it if you were using an ArrayList, leaving code here for reference, though we could use it instead of the above.
//        episodes = (ArrayList<String>) Arrays.asList(aContext.getResources().getStringArray(R.array.episodes));  //retrieving list of episodes predefined in strings-array "episodes" in strings.xml
//        episodeDescriptions = (ArrayList<String>) Arrays.asList(aContext.getResources().getStringArray(R.array.episode_descriptions));  //Also casting to a friendly ArrayList.


        episodeImages = new ArrayList<Integer>();   //Could also use helper function "getDrawables(..)" below to auto-extract drawable resources, but keeping things as simple as possible.
        episodeImages.add(R.drawable.st_spocks_brain);
        episodeImages.add(R.drawable.st_arena__kirk_gorn);
        episodeImages.add(R.drawable.st_this_side_of_paradise__spock_in_love);
        episodeImages.add(R.drawable.st_mirror_mirror__evil_spock_and_good_kirk);
        episodeImages.add(R.drawable.st_platos_stepchildren__kirk_spock);
        episodeImages.add(R.drawable.st_the_naked_time__sulu_sword);
        episodeImages.add(R.drawable.st_the_trouble_with_tribbles__kirk_tribbles);

        this.sp = sp;
    }

    public void sortByTitle() {
        Log.i("Warning", "Sort by title called");
        for (int i = 0; i < episodes.length; i++) {
            Log.i("before sorting", episodes[i].toString());
        }
        Arrays.sort(episodes);
//        Collections.sort(episodes);
        for (int i = 0; i < episodes.length; i++) {
            Log.i("after sorting", episodes[i].toString());
        }

    }

    public void sortByRating() {
        Log.i("Warning", "Sort by rating called");
    }

    //STEP 3: Override and implement getCount(..), ListView uses this to determine how many rows to render.
    @Override
    public int getCount() {
//        return episodes.size(); //all of the arrays are same length, so return length of any... ick!  But ok for now. :)
        return episodes.length;   //all of the arrays are same length, so return length of any... ick!  But ok for now. :)
        //Q: How else could we have done this (better)? ________________
    }

    //STEP 4: Override getItem/getItemId, we aren't using these, but we must override anyway.
    @Override
    public Object getItem(int position) {
//        return episodes.get(position);  //In Case you want to use an ArrayList
        return episodes[position];        //really should be returning entire set of row data, but it's up to us, and we aren't using this call.
    }

    @Override
    public long getItemId(int position) {
        return position;  //Another call we aren't using, but have to do something since we had to implement (base is abstract).
    }

    //THIS IS WHERE THE ACTION HAPPENS.  getView(..) is how each row gets rendered.
//STEP 5: Easy as A-B-C
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {  //convertView is Row (it may be null), parent is the layout that has the row Views.

//STEP 5a: Inflate the listview row based on the xml.
        View row;  //this will refer to the row to be inflated or displayed if it's already been displayed. (listview_row.xml)
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        row = inflater.inflate(R.layout.listview_row, parent, false);  //

// Let's optimize a bit by checking to see if we need to inflate, or if it's already been inflated...
        if (convertView == null){  //indicates this is the first time we are creating this row.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //Inflater's are awesome, they convert xml to Java Objects!
            row = inflater.inflate(R.layout.listview_row, parent, false);
        }
        else
        {
            row = convertView;
        }

//STEP 5b: Now that we have a valid row instance, we need to get references to the views within that row and fill with the appropriate text and images.
        ImageView imgEpisode = (ImageView) row.findViewById(R.id.imgEpisode);  //Q: Notice we prefixed findViewByID with row, why?  A: Row, is the container.
        TextView tvEpisodeTitle = (TextView) row.findViewById(R.id.tvEpisodeTitle);
        TextView tvEpisodeDescription = (TextView) row.findViewById(R.id.tvEpisodeDescription);

        tvEpisodeTitle.setText(episodes[position]);
        tvEpisodeDescription.setText(episodeDescriptions[position]);
//         tvEpisodeTitle.setText(episodes.get(position));
//         tvEpisodeDescription.setText(episodeDescriptions.get(position));
        imgEpisode.setImageResource(episodeImages.get(position).intValue());

        btnRandom = (Button) row.findViewById(R.id.btnRandom);
        final String randomMsg = ((Integer)position).toString() +": "+ episodeDescriptions[position];
//        final String randomMsg = ((Integer)position).toString() +": "+ episodeDescriptions.get(position);
        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, randomMsg, Toast.LENGTH_LONG).show();
            }
        });

        rbEpisode = (RatingBar) row.findViewById(R.id.rbEpisode);

        rbEpisode.setRating(sp.getFloat("pos" + String.valueOf(position),3f));
        Log.i("pos for rating", String.valueOf(position));
        rbEpisode.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putFloat("pos" + String.valueOf(position), rating);
                    editor.apply();
                }
            }
        });

//STEP 5c: That's it, the row has been inflated and filled with data, return it.
        return row;  //once the row is fully constructed, return it.  Hey whatif we had buttons, can we target onClick Events within the rows, yep!
//return convertView;

    }

    public String[] getEpisodes(){
        return episodes;
    }


    ///Helper method to get the drawables...///
    ///this might prove useful later...///

//    public ArrayList<Drawable> getDrawables() {
//        Field[] drawablesFields =com.example.sse.customlistview_sse.R.drawable.class.getFields();
//        ArrayList<Drawable> drawables = new ArrayList<Drawable>();
//
//        String fieldName;
//        for (Field field : drawablesFields) {
//            try {
//                fieldName = field.getName();
//                Log.i("LOG_TAG", "com.your.project.R.drawable." + fieldName);
//                if (fieldName.startsWith("animals_"))  //only add drawable resources that have our prefix.
//                    drawables.add(context.getResources().getDrawable(field.getInt(null)));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
}