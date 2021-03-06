package com.quartzodev.task;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.quartzodev.api.APIService;
import com.quartzodev.data.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victoraldir on 14/05/2017.
 */

public class SearchTask extends AsyncTaskLoader<List<Book>> {

    private final String LOG = SearchTask.class.getSimpleName();
    private boolean mCanceled = false;
    private String mQuery;
    private Integer mMaxResult;
    private List<Book> mData;

    public SearchTask(Context context) {
        super(context);
    }

    public SearchTask(Context context, String query, Integer maxResult) {
        super(context);
        mQuery = query;
        mMaxResult = maxResult;
    }


    @Override
    protected void onStartLoading() {
        if (mData != null) {
            // Use cached data
            deliverResult(mData);
        } else {
            // We have no data, so kick off loading it
            forceLoad();
        }
    }

    @Override
    public void deliverResult(List<Book> data) {
        // We’ll save the data for later retrieval
        mData = data;
        // We can do any pre-processing we want here
        // Just remember this is on the UI thread so nothing lengthy!
        super.deliverResult(data);
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
    }

    @Override
    protected boolean onCancelLoad() {
        Log.d(LOG, "onCancelLoad() fired!!!");
        return super.onCancelLoad();
    }

    @Override
    public List<Book> loadInBackground() {

        if (mQuery == null) return null;

        List<Book> bookList = new ArrayList<>();

        try {
            Thread.sleep(500); //Just to avoid unnecessary queries
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (isReset() || isLoadInBackgroundCanceled() || isAbandoned()) {
            Log.d(LOG, "This operation should be cancelled");
            Log.d(LOG, "isReset() : " + isReset());
            Log.d(LOG, "isAbandoned() : " + isAbandoned());
            Log.d(LOG, "isLoadInBackgroundCanceled() : " + isLoadInBackgroundCanceled());
            return null;
        }

        //MaxResult == 1 is a ISBN query TODO make it nicer!
        if (mMaxResult != null && mMaxResult == 1) {

            Book book = APIService.getInstance().getService(APIService.GOOGLE).getBookByISBN(mQuery);

            if(book == null){
                book = APIService.getInstance().getService(APIService.GOODREADS).getBookByISBN(mQuery);
            }

            if(book != null){
                bookList.add(book);
            }
        }

        //Not checking GOODREADS for now
        if (mMaxResult != null && mMaxResult > 1) {
            bookList = APIService.getInstance().getService(APIService.GOOGLE)
                    .getBooksMaxResult(mQuery, mMaxResult);
        }

        if (bookList != null && !mCanceled) {
            return bookList;
        }

        return null;
    }

}
