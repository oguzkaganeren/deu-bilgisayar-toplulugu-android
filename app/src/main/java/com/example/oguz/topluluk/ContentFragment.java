package com.example.oguz.topluluk;

/**
 * Created by Oguz on 08-Jun-17.
 */

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
    ImageView imageView;
    ArrayList<WebDataInfo> ls;
    WebDataInfo wb;

    WebDataAdapter myAdap;
    String[] myWebSource=new String[]{"http://www.techrepublic.com/rssfeeds/articles/latest/","https://www.wired.com/feed/rss","http://www.techradar.com/rss/news/software","https://www.cnet.com/rss/news/"};
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
        ls=new ArrayList<WebDataInfo>();
        ParseXMLTask pars=new ParseXMLTask();
        pars.execute();
      /*  ParseXMLTask pars2=new ParseXMLTask();
        pars2.execute("http://www.techrepublic.com/rssfeeds/articles/latest/");
        ParseXMLTask pars3=new ParseXMLTask();
        pars3.execute("https://www.cnet.com/rss/news/");
        ParseXMLTask pars4=new ParseXMLTask();
        pars4.execute("http://feeds.pcworld.com/pcworld/latestnews");*/
        //https://servis.chip.com.tr/chiponline.xml
        //http://www.techrepublic.com/rssfeeds/articles/latest/
        //https://www.cnet.com/rss/news/
        //http://feeds.pcworld.com/pcworld/latestnews
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
        mRecyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(mLinearLayoutManager) {
            @Override
            //scroll yaptıkça verileri çekmesi gerekiyor bunun için aşağıdaki methodu kullanacağım
            public void onLoadMore(int current_page) {
                if (mySourceNumber<myWebSource.length) {
                    ParseXMLTask pars = new ParseXMLTask();
                    pars.execute();
                }
            }
        });
        return v;
    }


    public String stripHtml(String html) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return Html.fromHtml(html).toString();
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