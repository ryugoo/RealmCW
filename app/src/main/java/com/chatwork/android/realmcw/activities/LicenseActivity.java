package com.chatwork.android.realmcw.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chatwork.android.realmcw.R;
import com.chatwork.android.realmcw.models.License;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class LicenseActivity extends ActionBarActivity {
    @InjectView(R.id.list_license)
    public ListView mListView;

    /**
     * Create "LicenseActivity" intent object
     *
     * @param context Context object
     * @return Intent object
     */
    public static Intent createIntent(final Context context) {
        return new Intent(context, LicenseActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
        ButterKnife.inject(this);

        // Set license data
        final ArrayList<License> licenses = new ArrayList<>();
        licenses.add(new License("ActiveAndroid", "Michael Pardo", "2010", License.Type.APACHE_V2));
        licenses.add(new License("Butter Knife", "Jake Wharton", "2013", License.Type.APACHE_V2));
        licenses.add(new License("EventBus", "greenrobot", "2012", License.Type.APACHE_V2));
        licenses.add(new License("google-gson", "Google", "2008", License.Type.APACHE_V2));
        licenses.add(new License("OkHttp", "Square", "2013", License.Type.APACHE_V2));
        licenses.add(new License("Picasso", "Square", "2013", License.Type.APACHE_V2));
        licenses.add(new License("Realm", "Realm", "2014", License.Type.APACHE_V2));
        licenses.add(new License("Retrolambda", "Esko Luontola", "2013", License.Type.APACHE_V2));
        licenses.add(new License("Gradle Retrolambda Plugin", "Evan Tatarka", "2014", License.Type.APACHE_V2));

        // Set adapter
        final LicenseAdapter adapter = new LicenseAdapter(getApplicationContext(), licenses);
        mListView.setAdapter(adapter);
    }

    public static class LicenseAdapter extends ArrayAdapter<License> {
        private Context mContext;
        private LayoutInflater mInflater;

        public LicenseAdapter(Context context, List<License> licenses) {
            super(context, 0, licenses);
            mContext = context;
            mInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            final License license = getItem(position);
            final LicenseViewHolder licenseViewHolder;

            if (view == null) {
                view = mInflater.inflate(R.layout.list_license, new RelativeLayout(mContext), true);
                licenseViewHolder = new LicenseViewHolder(view);
                view.setTag(licenseViewHolder);
            } else {
                licenseViewHolder = (LicenseViewHolder) view.getTag();
            }

            licenseViewHolder.licenseName.setText(license.getName());
            licenseViewHolder.licenseBody.setText(String.format(mContext.getString(R.string.license_apache_v2), license.getYear(), license.getAuthor()));

            return view;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        public static class LicenseViewHolder {
            @InjectView(R.id.license_name)
            public TextView licenseName;
            @InjectView(R.id.license_body)
            public TextView licenseBody;

            public LicenseViewHolder(final View view) {
                ButterKnife.inject(this, view);
            }
        }
    }
}
