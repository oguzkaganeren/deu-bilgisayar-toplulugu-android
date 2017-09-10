package com.example.oguz.bilgisayarToplulugu;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ContentFragment extends Fragment {
    //projenin en karışık kısmı bu kısmı daha anlaşılır biçime sokmak gerek birde rss yerine json'a geçilmeli
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private List headlines;
    private List links;
    private ImageView imageView;
    private ArrayList<WebDataInfo> ls;
    private WebDataInfo wb;
    private WebDataAdapter myAdap;
    //veri çekilecek kaynaklar
    private String[] myWebSource=new String[]{"https://www.cnet.com/rss/news/","http://www.techradar.com/rss/news/software","https://www.wired.com/feed/rss"};
    private int mySourceNumber=0;//endless rcyler sayesinde aşağılara inildikçe yeni kaynaktan veri çekecek, bu sayı arttıkça bir sonraki kaynağa geçer
    public ContentFragment() {
        // Required empty public constructor
    }
    //arkaplanda verileri çekmek için kullanılan inner class
    class ParseXMLTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                    URL urlTo = new URL(myWebSource[mySourceNumber]);//sourcenumber'a göre hangi web sayfasından veri alınacağı belirleniyor
                if (mySourceNumber<myWebSource.length)//sınırı aşmayalım :)
                    mySourceNumber++;
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(false);//içerisinde bulunan boşlukları kaldırması için kullanılır(bi kontrol et)
                    XmlPullParser xpp = factory.newPullParser();

                    // We will get the XML from an input stream
                    xpp.setInput(getInputStream(urlTo), "UTF_8");
                    boolean insideItem = false;//node'un cocuklarına erişmek için kullanacağız
                    // Returns the type of current event: START_TAG, END_TAG, etc..
                    int eventType = xpp.getEventType();
                    wb = new WebDataInfo();
                    String source=urlTo.toString();
                    String[] sourceWeb=source.split("\\.");//kaynağı cardview'e yazdırırken kullanıldı
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_TAG) {
                            //genel olarak image için üç farklı tag kullanılıyor enclosure,media:content,media:thumbnail
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
                            //gelen veriler önce wb classına alınıyor burda listeye aktarılıyor
                            ls.add(wb);
                            //wb'nin yeni instance alınıyor(önceki içeriğin sıfırlanması için)(belki değiştir)
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
            //background işlemi bitti
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
        //internet olup olmadığı kısmı tam iyi çalışmıyor düzeltilecek
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

        // 3.
        mRecyclerView = (RecyclerView) v.findViewById(R.id.rcview);
        mRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        final View myToolBar = getActivity().findViewById(R.id.toolbar);
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
            //burayı bi kontrol et
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