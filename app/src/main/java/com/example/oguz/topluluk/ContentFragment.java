package com.example.oguz.topluluk;

/**
 * Created by Oguz on 08-Jun-17.
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ContentFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private List headlines;
    private List links;

    WebDataInfo wb;
    WebDataAdapter myAdap;
    class ParseXMLTask extends AsyncTask<String, Integer, Object> {
        ArrayList<WebDataInfo> ls=new ArrayList<WebDataInfo>();
        @Override
        protected Object doInBackground(String... params) {
            try {
                URL urlTo = new URL("http://feeds.pcworld.com/pcworld/latestnews");

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

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {

                        if (xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = true;
                        } else if (xpp.getName().equalsIgnoreCase("title")) {
                            if (insideItem) {
                                wb = new WebDataInfo();
                                wb.title = xpp.nextText();
                                ls.add(wb);
                            }
                        }else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                            if (insideItem) {
                                wb = new WebDataInfo();
                                wb.date = xpp.nextText();
                                ls.add(wb);
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("link")) {
                            if (insideItem) {
                                wb = new WebDataInfo();
                                wb.link = xpp.nextText();
                               ls.add(wb);
                            }
                        }
                    }else if(eventType==XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")){
                        insideItem=false;
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
            return ls;
        }
        public ArrayList<WebDataInfo> getMyLs()
        {
            return ls;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

    }
    public ContentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseXMLTask pars=new ParseXMLTask();
        pars.execute();
       ArrayList<WebDataInfo> myList= pars.getMyLs();

        myAdap=new WebDataAdapter(myList);
      //  mRecyclerView.setAdapter(myAdap);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        //bu kısım fragmenlere özel
        View v = inflater.inflate(R.layout.fragment_content, parent, false);

        // 2.
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        // 3.
        mRecyclerView = (RecyclerView) v.findViewById(R.id.rcview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
       mRecyclerView.setAdapter(myAdap);

        return v;
    }

    private List<WebDataInfo> createContent(){
        List<WebDataInfo> ls=new ArrayList<WebDataInfo>();
        // Initializing instance variables
        headlines = new ArrayList();
        links = new ArrayList();

        try {
            URL url = new URL("http://feeds.pcworld.com/pcworld/latestnews");

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();
            // We will get the XML from an input stream
            xpp.setInput(getInputStream(url), "UTF_8");
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
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("item")) {
                        insideItem = true;
                    } else if (xpp.getName().equalsIgnoreCase("title")) {
                        if (insideItem) {
                            WebDataInfo wb=new WebDataInfo();
                            wb.title=xpp.nextText();
                            ls.add(wb);
                            //headlines.add(xpp.nextText()); //extract the headline
                        }
                    } else if (xpp.getName().equalsIgnoreCase("link")) {
                        if (insideItem)
                        {
                            WebDataInfo wb=new WebDataInfo();
                            wb.link=xpp.nextText();
                            ls.add(wb);
                            //links.add(xpp.nextText()); //extract the link of article
                        }

                    }
                }else if(eventType==XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")){
                    insideItem=false;
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
        /*for (int i=0;i<10;i++)
        {
            WebDataInfo wb=new WebDataInfo();
            wb.title="Deneme";
            wb.link="link";
            wb.imgSrc=R.drawable.about_24dp;
            wb.description="Sometimes, the easiest way to get inspired is to see how others generate content.  Here are some resources that are full of examples to get the juices flowing.";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String currentDateandTime = sdf.format(new Date());
            wb.date=currentDateandTime;
            ls.add(wb);
        }*/

        return ls;
    }
    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            return null;
        }
    }
}