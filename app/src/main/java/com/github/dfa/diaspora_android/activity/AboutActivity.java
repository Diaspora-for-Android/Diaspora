/*
    This file is part of the dandelion*.

    dandelion* is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    dandelion* is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with the dandelion*.

    If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.dfa.diaspora_android.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dfa.diaspora_android.App;
import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.listener.IntellihideToolbarActivityListener;
import com.github.dfa.diaspora_android.ui.HtmlTextView;
import com.github.dfa.diaspora_android.ui.theme.ThemeHelper;
import com.github.dfa.diaspora_android.ui.theme.ThemedActivity;
import com.github.dfa.diaspora_android.ui.theme.ThemedFragment;
import com.github.dfa.diaspora_android.util.AppLog;
import com.github.dfa.diaspora_android.util.AppSettings;
import com.github.dfa.diaspora_android.util.Helpers;

import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Activity that holds some fragments that show information about the app in a tab layout
 */
public class AboutActivity extends ThemedActivity
        implements IntellihideToolbarActivityListener {

    @BindView(R.id.about__appbar)
    protected AppBarLayout appBarLayout;

    @BindView(R.id.main__topbar)
    protected Toolbar toolbar;

    @BindView(R.id.appbar_linear_layout)
    protected LinearLayout linearLayout;

    @BindView(R.id.tabs)
    protected TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about__activity);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24px));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutActivity.this.onBackPressed();
            }
        });
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = ButterKnife.findById(this, R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getAppSettings().isIntellihideToolbars()) {
            this.enableToolbarHiding();
        } else {
            this.disableToolbarHiding();
        }
    }

    @Override
    protected void applyColorToViews() {
        ThemeHelper.updateToolbarColor(toolbar);
        ThemeHelper.updateTabLayoutColor(tabLayout);
        ThemeHelper.setPrimaryColorAsBackground(linearLayout);
    }

    @Override
    public void enableToolbarHiding() {
        AppLog.d(this, "Enable Intellihide");
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) linearLayout.getLayoutParams();
        //scroll|enterAlways|snap
        params.setScrollFlags(toolbarDefaultScrollFlags);
        appBarLayout.setExpanded(true, true);
    }

    @Override
    public void disableToolbarHiding() {
        AppLog.d(this, "Disable Intellihide");
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) linearLayout.getLayoutParams();
        params.setScrollFlags(0);  // clear all scroll flags
        appBarLayout.setExpanded(true, true);
    }

    /**
     * Fragment that shows general information about the app
     */
    public static class AboutFragment extends ThemedFragment {

        public static final String TAG = "com.github.dfa.diaspora_android.AboutActivity.AboutFragment";

        @BindView(R.id.fragment_about__app_version)
        TextView appVersion;

        @BindView(R.id.fragment_about__spread_the_word_text)
        HtmlTextView spreadText;

        @BindView(R.id.fragment_about__contribute_button)
        Button contributeBtn;

        @BindView(R.id.fragment_about__translate_button)
        Button translateBtn;

        @BindView(R.id.fragment_about__feedback_button)
        Button feedbackBtn;

        @BindView(R.id.fragment_about__spread_the_word_button)
        Button spreadBtn;

        public AboutFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.about__fragment_about, container, false);
            ButterKnife.bind(this, rootView);
            if (isAdded()) {
                try {
                    PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                    appVersion.setText(getString(R.string.fragment_debug__app_version, pInfo.versionName + " (" + pInfo.versionCode + ")"));

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return rootView;
        }

        @Override
        protected void applyColorToViews() {
            ThemeHelper.getInstance(getAppSettings());
            ThemeHelper.updateTextViewLinkColor(spreadText);
            ThemeHelper.updateButtonTextColor(contributeBtn);
            ThemeHelper.updateButtonTextColor(feedbackBtn);
            ThemeHelper.updateButtonTextColor(spreadBtn);
            ThemeHelper.updateButtonTextColor(translateBtn);
        }

        @Override
        public String getFragmentTag() {
            return TAG;
        }

        @Override
        public void onCreateBottomOptionsMenu(Menu menu, MenuInflater inflater) {
            /* Nothing to do */
        }

        @Override
        public boolean onBackPressed() {
            return false;
        }

        @OnClick({R.id.fragment_about__contribute_button, R.id.fragment_about__translate_button, R.id.fragment_about__feedback_button, R.id.fragment_about__spread_the_word_button})
        public void buttonClicked(View view) {
            switch (view.getId()) {
                case R.id.fragment_about__contribute_button:
                    Helpers.openInExternalBrowser(getContext(), getString(R.string.fragment_about__contribute_link));
                    break;
                case R.id.fragment_about__translate_button:
                    Helpers.openInExternalBrowser(getContext(), getString(R.string.fragment_about__translate_link));
                    break;
                case R.id.fragment_about__feedback_button:
                    Helpers.openInExternalBrowser(getContext(), getString(R.string.fragment_About__feedback_link));
                    break;
                case R.id.fragment_about__spread_the_word_button:
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.fragment_about__spread_the_word_share_text, getString(R.string.fragment_about__fdroid_link)));
                    startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.action_share_dotdotdot)));
                    break;
            }
        }
    }

    /**
     * Fragment that shows information about the license of the app and used 3rd party libraries
     */
    public static class LicenseFragment extends ThemedFragment {
        public static final String TAG = "com.github.dfa.diaspora_android.AboutActivity.LicenseFragment";

        @BindView(R.id.fragment_license__maintainers_text)
        HtmlTextView maintainers;

        @BindView(R.id.fragment_license__contributors_text)
        HtmlTextView contributors;

        @BindView(R.id.fragment_license__thirdparty_libs_text)
        HtmlTextView thirdPartyLibs;

        @BindView(R.id.fragment_license__license_button)
        Button licenseBtn;

        @BindView(R.id.fragment_license__leafpic_button)
        Button leafpicBtn;

        private String accentColor;


        public LicenseFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.about__fragment_license, container, false);
            ButterKnife.bind(this, rootView);
            final Context context = rootView.getContext();
            accentColor = Helpers.colorToHex(ThemeHelper.getAccentColor());

            maintainers.setTextFormatted(getString(R.string.fragment_license__maintainers_text, getMaintainersHtml(context)));
            contributors.setTextFormatted(getString(R.string.fragment_license__contributors_thank_you, getContributorsHtml(context)));
            thirdPartyLibs.setTextFormatted(getLicense3dPartyHtml(context));
            return rootView;
        }

        @OnClick({R.id.fragment_license__leafpic_button, R.id.fragment_license__license_button})
        public void buttonClicked(View v) {
            switch (v.getId()) {
                case R.id.fragment_license__leafpic_button:
                    Helpers.openInExternalBrowser(getContext(), getString(R.string.fragment_licesen__misc_leafpic_link));
                    break;
                case R.id.fragment_license__license_button:
                    Helpers.openInExternalBrowser(getContext(), getString(R.string.fragment_license__license_gpl_link));
                    break;
            }
        }

        public String getContributorsHtml(Context context) {
            return Helpers.readTextfileFromRawRessource(context, R.raw.contributors,
                    "<font color='" + accentColor + "'><b>*</b></font> ", "<br>");
        }

        public String getMaintainersHtml(Context context) {
            String text = Helpers.readTextfileFromRawRessource(context, R.raw.maintainers, "", "<br>");
            text = text
                    .replace("NEWENTRY", "<font color='" + accentColor + "'><b>*</b></font> ")
                    .replace("SUBTABBY", "&nbsp;&nbsp;");
            return text;
        }

        public String getLicense3dPartyHtml(Context context) {
            String text = Helpers.readTextfileFromRawRessource(context, R.raw.license_third_party, "", "<br>");
            text = text.replace("NEWENTRY", "<font color='" + accentColor + "'><b>*</b></font> ");
            return text;
        }

        @Override
        protected void applyColorToViews() {
            ThemeHelper.getInstance(getAppSettings());
            ThemeHelper.updateButtonTextColor(leafpicBtn);
            ThemeHelper.updateButtonTextColor(licenseBtn);
            ThemeHelper.updateTextViewLinkColor(maintainers);
            ThemeHelper.updateTextViewLinkColor(thirdPartyLibs);
        }

        @Override
        public String getFragmentTag() {
            return TAG;
        }

        @Override
        public void onCreateBottomOptionsMenu(Menu menu, MenuInflater inflater) {
            /* Nothing to do */
        }

        @Override
        public boolean onBackPressed() {
            return false;
        }
    }

    /**
     * Fragment that shows debug information like app version, pod version...
     */
    public static class DebugFragment extends Fragment implements Observer {
        public static final String TAG = "com.github.dfa.diaspora_android.AboutActivity.DebugFragment";

        @BindView(R.id.fragment_debug__package_name)
        TextView packageName;

        @BindView(R.id.fragment_debug__app_version)
        TextView appVersion;

        @BindView(R.id.fragment_debug__android_version)
        TextView osVersion;

        @BindView(R.id.fragment_debug__device_name)
        TextView deviceName;

        @BindView(R.id.fragment_debug__account_profile_name)
        TextView podName;

        @BindView(R.id.fragment_debug__account_profile_domain)
        TextView podDomain;

        @BindView(R.id.fragment_debug__log_box)
        TextView logBox;

        public DebugFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.about__fragment_debug, container, false);
            ButterKnife.bind(this, rootView);
            App app = (App) getActivity().getApplication();
            logBox.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (isAdded()) {
                        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("DEBUG_LOG", AppLog.Log.getLogBuffer());
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(DebugFragment.this.getActivity(), R.string.fragment_debug__toast_log_copied, Toast.LENGTH_SHORT).show();
                    } else {
                        AppLog.d(this, "Not Added!");
                    }
                    return true;
                }
            });
            AppLog.Log.addLogObserver(this);
            update(AppLog.Log.getInstance(), null);

            if (isAdded()) {
                try {
                    PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                    AppSettings appSettings = ((App) getActivity().getApplication()).getSettings();
                    packageName.setText(pInfo.packageName);
                    appVersion.setText(getString(R.string.fragment_debug__app_version, pInfo.versionName + " (" + pInfo.versionCode + ")"));

                    osVersion.setText(getString(R.string.fragment_debug__android_version, Build.VERSION.RELEASE));
                    deviceName.setText(getString(R.string.fragment_debug__device_name, Build.MANUFACTURER + " " + Build.MODEL));
                    if (app.getSettings().getPod() != null) {
                        podDomain.setText(getString(R.string.fragment_debug__pod_profile_url, app.getSettings().getPod().getPodUrl()));
                        podName.setText(getString(R.string.fragment_debug__pod_profile_name, app.getSettings().getPod().getName()));
                    }

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

            }
            return rootView;
        }

        @Override
        public void onDestroyView() {
            AppLog.Log.removeLogObserver(this);
            super.onDestroyView();
        }

        @Override
        public void update(Observable observable, Object o) {
            if (logBox != null) {
                logBox.setText(AppLog.Log.getLogBuffer());
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: //About
                    return new AboutFragment();
                case 1: //License
                    return new LicenseFragment();
                case 2: //Debug
                default:
                    return new DebugFragment();
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.about_activity__title_about_app);
                case 1:
                    return getString(R.string.about_activity__title_about_license);
                case 2:
                    return getString(R.string.about_activity__title_debug_info);
            }
            return null;
        }
    }
}
