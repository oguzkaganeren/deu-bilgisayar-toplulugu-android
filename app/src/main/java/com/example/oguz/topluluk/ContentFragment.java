package com.example.oguz.topluluk;

/**
 * Created by Oguz on 08-Jun-17.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ContentFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private List headlines;
    private List links;
    ImageView imageView;
    ArrayList<WebDataInfo> ls;
    WebDataInfo wb;
   public WebDataAdapter myAdap;
        String[] myWebSource=new String[]{"https://www.wired.com/feed/rss","http://www.techradar.com/rss/news/software","https://www.cnet.com/rss/news/"};
    int mySourceNumber=0;
    public ContentFragment() {
        // Required empty public constructor
    }

    class ParseXMLTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                    URL urlTo = new URL(myWebSource[mySourceNumber]);
                if (mySourceNumber<myWebSource.length)
                    mySourceNumber++;
                    //https://servis.chip.com.tr/chiponline.xml
                    //http://www.techrepublic.com/rssfeeds/articles/latest/
                    //https://www.cnet.com/rss/news/
                    //http://feeds.pcworld.com/pcworld/latestnews
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(false);
                    XmlPullParser xpp = factory.newPullParser();

                    // We will get the XML from an input stream
                    xpp.setInput(getInputStream(urlTo), "UTF_8");

        /* We will parse the XML content looking for the "<title>" tag which appears inside the "<item>" tag.
         * However, we should take in consideration that the rss feed name also is enclosed in a "<title>" tag.
         * As we know, every feed begins with these lines: "<channel><title>Feed_Name</title>...."
         * so we should skip the "<title>" tag which is a child of "<channel>" tag,
         * and take in consideration only "<title>" tag which is a child of "<item>"
         *
         * In order to achieve this, we will make use of a boolean variable.
         */
                    boolean insideItem = false;
                    // Returns the type of current event: START_TAG, END_TAG, etc..
                    int eventType = xpp.getEventType();
                    wb = new WebDataInfo();
                    String source=urlTo.toString();

                    String[] sourceWeb=source.split("\\.");
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_TAG) {

                            if (xpp.getName().equalsIgnoreCase("item")) {
                                insideItem = true;

                            } else if (xpp.getName().equalsIgnoreCase("title")) {
                                if (insideItem) {
                                    wb.title = xpp.nextText();
                                }
                            } else if (xpp.getName().equalsIgnoreCase("enclosure")) {
                                if (insideItem) {
                                    wb.imgSrc = xpp.getAttributeValue(null, "url");

                                }
                            } else if (xpp.getName().equalsIgnoreCase("media:content")) {
                                if (insideItem) {
                                    wb.imgSrc = xpp.getAttributeValue(null, "url");

                                }
                            } else if (xpp.getName().equalsIgnoreCase("media:thumbnail")) {
                                if (insideItem) {
                                    wb.imgSrc = xpp.getAttributeValue(null, "url");

                                }
                            } else if (xpp.getName().equalsIgnoreCase("description")) {
                                if (insideItem) {
                                    wb.description = stripHtml(xpp.nextText());
                                }
                            } else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                                if (insideItem) {
                                    wb.date = xpp.nextText();
                                }
                            } else if (xpp.getName().equalsIgnoreCase("link")) {
                                if (insideItem) {
                                    wb.link = xpp.nextText();
                                    wb.source=sourceWeb[1];
                                }
                            }
                        } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = false;
                            ls.add(wb);
                            wb = new WebDataInfo();
                        }

                        eventType = xpp.next(); //move to next element
                    }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //do something after parsing is done
            myAdap=new WebDataAdapter(getActivity(),ls);
            mRecyclerView.swapAdapter(myAdap,false);
        }
        public ArrayList<WebDataInfo> getMyLs()
        {
            return ls;
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Type type = new TypeToken<ArrayList<WebDataInfo>>(){}.getType();
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity().getApplicationContext());
        Gson gson = new Gson();
        String json = appSharedPrefs.getString("topluveri", "");
        if (!haveInternet()){
            ls=gson.fromJson(json,type);
            myAdap=new WebDataAdapter(getActivity(),ls);
            Log.e("internet","yoktur");
        }else{
            ls=new ArrayList<WebDataInfo>();
            ParseXMLTask pars=new ParseXMLTask();
            pars.execute();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        //bu kısım fragmenlere özel

        View v = inflater.inflate(R.layout.fragment_content, parent, false);
        v.findViewById(R.id.thumbnail).setDrawingCacheEnabled(true);
        imageView=new ImageView(getActivity());

        // 2.
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        // 3.
        mRecyclerView = (RecyclerView) v.findViewById(R.id.rcview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        final View myToolBar = getActivity().findViewById(R.id.toolbar);
       // final View myTabs = getActivity().findViewById(R.id.tabs);
       // final View myBarLa = getActivity().findViewById(R.id.myBarLayout);
        if (mRecyclerView.getAdapter()==null){
            mRecyclerView.swapAdapter(myAdap,false);
        }
        mRecyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(mLinearLayoutManager) {
            @Override
            //scroll yaptıkça verileri çekmesi gerekiyor bunun için aşağıdaki methodu kullanacağım
            public void onLoadMore(int current_page) {
                if (mySourceNumber<myWebSource.length) {
                    ParseXMLTask pars = new ParseXMLTask();
                    pars.execute();
                }
            }
            private static final int HIDE_THRESHOLD = 20;
            private int scrolledDistance = 0;
            private boolean controlsVisible = true;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                    hideViews();
                    controlsVisible = false;
                    scrolledDistance = 0;
                } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                    showViews();
                    controlsVisible = true;
                    scrolledDistance = 0;
                }

                if((controlsVisible && dy>0) || (!controlsVisible && dy<0)) {
                    scrolledDistance += dy;
                }
            }

            private void hideViews() {
              // myToolBar.animate().translationY(-myToolBar.getHeight()).setInterpolator(new AccelerateInterpolator(3));
               // myTabs.animate().translationY(-myTabs.getHeight()).setInterpolator(new AccelerateInterpolator(2));


            }

            private void showViews() {
               //myToolBar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(1));
               // myTabs.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
            }
        });

        return v;
    }
    @Override
    public void onStop() {
        // call the superclass method first
        super.onStop();
        //uygulamadan çıkarken indirilen içerikleri telefona kayıt ediyoruz
        //çünkü biz üyelerimizin internet kotalarını düşünürüz :)
        if (ls!=null)
        {
            SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity().getApplicationContext());
            SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(ls);
            prefsEditor.putString("topluveri", json);
            prefsEditor.commit();
            Log.d("Kaydetti",json);
        }

    }

    public String stripHtml(String html) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return Html.fromHtml(html).toString();
        }
    }
    public Boolean haveInternet(){
        ConnectivityManager conMgr = (ConnectivityManager) getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if (i == null)
            return false;
        if (!i.isConnected())
            return false;
        if (!i.isAvailable())
            return false;
        return true;
    }
    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            return null;
        }
    }
}