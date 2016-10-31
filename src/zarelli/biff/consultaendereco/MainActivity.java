package zarelli.biff.consultaendereco;

import zarelli.biff.consultaendereco.screens.about.FragAbout;
import zarelli.biff.consultaendereco.screens.consult.FragConsult;
import zarelli.biff.consultaendereco.screens.myplaces.FragMyPlaces;
import zarelli.biff.consultaendereco.screens.whereami.FragWhereAmI;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity implements
		ListView.OnItemClickListener {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private String[] titulosMenu;
	private int index;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		titulosMenu = getResources().getStringArray(R.array.menus);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, titulosMenu));

		mDrawerList.setOnItemClickListener(this);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			@Override
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(getTitle());
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(R.string.menu);
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			index = -1;
			selectItem(0);
		} else {
			index = savedInstanceState.getInt("index");
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("index", index);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		selectItem(position);
	}

	private void selectItem(int position) {
		index = position;
		FragmentManager fragmentManager = getSupportFragmentManager();

		String tag = String.valueOf(position);
		Fragment fragment = null;
		try {
			fragment = fragmentManager.findFragmentByTag(tag);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (fragment == null) {
			switch (position) {
			case 0:
				fragment = new FragConsult();
				break;
			case 1:
				fragment = new FragWhereAmI();
				break;
			case 2:
				fragment = new FragMyPlaces();
				break;
			case 3:
				fragment = new FragAbout();
				break;
			}
		}

		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment, tag).commit();

		mDrawerList.setItemChecked(position, true);
		mDrawerList.setSelection(position);
		setTitle(titulosMenu[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onBackPressed() {
		if (index > 0) {
			selectItem(0);
		} else {
			super.onBackPressed();
		}
	}
}
