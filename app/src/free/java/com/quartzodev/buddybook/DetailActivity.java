package com.quartzodev.buddybook;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.quartzodev.data.Book;
import com.quartzodev.data.FirebaseDatabaseHelper;
import com.quartzodev.fragments.DetailActivityFragment;
import com.quartzodev.utils.DialogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DetailActivity extends AppCompatActivity implements
        DetailActivityFragment.OnDetailInteractionListener,
        FirebaseDatabaseHelper.OnPaidOperationListener {

    public static final String ARG_BOOK_ID = "bookId";
    public static final String ARG_FOLDER_ID = "folderId";
    public static final String ARG_USER_ID = "userId";
    public static final String ARG_FOLDER_LIST_ID = "folderListId";
    public static final String ARG_BOOK_JSON = "bookJson";
    public static final String ARG_FLAG_LEND_OPERATION = "flagLendOperation";
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.detail_coordinator)
    CoordinatorLayout mCoordinatorLayout;
    private FirebaseDatabaseHelper mFirebaseDatabaseHelper;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirebaseDatabaseHelper = FirebaseDatabaseHelper.getInstance();

        String bookId = getIntent().getExtras().getString(ARG_BOOK_ID);
        String folderId = getIntent().getExtras().getString(ARG_FOLDER_ID);
        mUserId = getIntent().getExtras().getString(ARG_USER_ID);
        String folderListId = getIntent().getExtras().getString(ARG_FOLDER_LIST_ID);
        String bookJson = getIntent().getExtras().getString(ARG_BOOK_JSON);
        boolean flagLendOp = getIntent().getExtras().getBoolean(ARG_FLAG_LEND_OPERATION);

        DetailActivityFragment newFragment = DetailActivityFragment.newInstance(mUserId, bookId, folderId, folderListId, bookJson, flagLendOp);

        getSupportFragmentManager().popBackStackImmediate();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.detail_container, newFragment).commit();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            supportFinishAfterTransition();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onLendBook(Book bookApi) {
        DialogUtils.alertDialogLendBook(this, mCoordinatorLayout, mFirebaseDatabaseHelper, mUserId, bookApi);
    }

    @Override
    public void onReturnBook(Book bookApi) {
        DialogUtils.alertDialogReturnBook(this, mFirebaseDatabaseHelper, mUserId, bookApi);
    }

    public void loadBook(Book book) {
        ((DetailActivityFragment) getSupportFragmentManager().findFragmentById(R.id.detail_container)).loadBook(book);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onInsertBook(boolean success) {
        if (!success) {
            DialogUtils.alertDialogUpgradePro(this);
        } else {
            Snackbar.make(mCoordinatorLayout, getString(R.string.insert_success), Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onInsertFolder(boolean success) {
        if (!success) {
            DialogUtils.alertDialogUpgradePro(this);
        }
    }
}
