package com.example.oguz.topluluk;

/**
 * Created by Oguz on 08-Jun-17.
 */

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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
    public ContentFragment() {
        // Required empty public constructor
    }

    class ParseXMLTask extends AsyncTask<String, Void, Void> {
        ArrayList<WebDataInfo> ls=new ArrayList<WebDataInfo>();
        @Override
        protected Void doInBackground(String... params) {
            try {
                URL urlTo = new URL("http://feeds.pcworld.com/pcworld/latestnews");
                //https://servis.chip.com.tr/chiponline.xml
                //http://www.techrepublic.com/rssfeeds/articles/latest/
                //https://www.cnet.com/rss/news/
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
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {

                        if (xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = true;

                        } else if (xpp.getName().equalsIgnoreCase("title")) {
                            if (insideItem) {
                                wb.title = xpp.nextText();
                            }
                        }else if (xpp.getName().equalsIgnoreCase("enclosure")) {
                            if (insideItem) {
                                wb.imgSrc = LoadImageFromWebOperations(xpp.getAttributeValue(null,"url"));

                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("media:content")) {
                            if (insideItem) {
                                wb.imgSrc = LoadImageFromWebOperations(xpp.getAttributeValue(null,"url"));

                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("media:thumbnail")) {
                            if (insideItem) {
                                wb.imgSrc = LoadImageFromWebOperations(xpp.getAttributeValue(null,"url"));

                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("description")) {
                            if (insideItem) {
                                wb.description = stripHtml(xpp.nextText());
                            }
                        }else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                            if (insideItem) {
                                wb.date = xpp.nextText();
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("link")) {
                            if (insideItem) {
                                wb.link = xpp.nextText();

                            }
                        }
                    }else if(eventType==XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")){
                        insideItem=false;
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
            myAdap=new WebDataAdapter(ls);
            mRecyclerView.setAdapter(myAdap);
        }
        public ArrayList<WebDataInfo> getMyLs()
        {
            return ls;
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseXMLTask pars=new ParseXMLTask();
        pars.execute();
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
    public String stripHtml(String html) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return Html.fromHtml(html).toString();
        }
    }
    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }
    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            return null;
        }
    }
}