package com.sample.covid19;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback{


    private GoogleMap mMap;
    private Marker currentMarker = null;
    SupportMapFragment mapFragment;
    SearchView searchView;
    private ClusterManager<MyItem> clusterManager;
    ArrayList<Clinic> clinics;
    ArrayList<Location> clinic_address;
    Context context = this;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 100000000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500000000; // 0.5초
    static ArrayList<Road> tmpRoadArray = new ArrayList<Road>();
    private DrawerLayout drawerLayout;
    private View drawerView;


    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용됩니다.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;


    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소


    Location mCurrentLocatiion;
    LatLng currentPosition;


    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;



    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.
    // (참고로 Toast에서는 Context가 필요했습니다.)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        TextView tv = (TextView)findViewById(R.id.tv);
        tv.setSelected(true);


        /*AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL jun = new URL("http://192.168.40.3:80/info.php");
                    URLConnection URLconnection = jun.openConnection();
                    HttpURLConnection myConnection = (HttpURLConnection) URLconnection;
                    Log.d(TAG, Integer.toString(myConnection.getResponseCode()));
                    if(myConnection.getResponseCode()==200){
                        InputStream responseBody = myConnection.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                        BufferedReader bufferedReader = new BufferedReader(responseBodyReader);

                        StringBuilder sb = new StringBuilder();
                        String line;

                        while((line = bufferedReader.readLine()) != null){
                            sb.append(line);
                        }
                        bufferedReader.close();
                        Log.d(TAG, "msg:"+sb.toString().trim());
                        try{
                            JSONObject jsonObject = new JSONObject(sb.toString().trim());
                            JSONArray jsonArray = jsonObject.getJSONArray("road");
                            Log.d(TAG, "msg" + jsonArray.length());

                            for(int i=0 ; i<jsonArray.length(); i++){
                                Road tmpRoad = new Road();
                                JSONObject item = jsonArray.getJSONObject(i);
                                tmpRoad.setNumber(item.getString("넘버"));
                                tmpRoad.setAddress(item.getString("주소"));
                                tmpRoad.setLat(item.getDouble("위도"));
                                tmpRoad.setLng(item.getDouble("경도"));
                                tmpRoadArray.add(tmpRoad);
                            }
                        } catch (JSONException e){
                            Log.d(TAG, "tmpRoadArray" + e);

                            e.printStackTrace();
                        }

                        for(Road i : tmpRoadArray) {
                            Log.d(TAG, "위도" + i.getLat() +"경도"+i.getLng());
                        }
                    }
                    else{
                        Log.d(TAG, "msg:eee");

                    }
                }
                catch(Exception e){
                    Log.d(TAG, "error"+e);
                }
            }
        });
        */



        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerView = (View)findViewById(R.id.drawer);

        Button btn_open = (Button)findViewById(R.id.btn_open);
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(drawerView);
            }
        });

        Button btn_close = (Button)findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawers();
            }
        });

        Button btn_now = (Button)findViewById(R.id.btn_now);
        btn_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,xxx.class);
                startActivity(intent); //액티비티 이동.
            }
        });

        Button btn_mask = (Button)findViewById(R.id.btn_mask);
        btn_mask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,mmm.class);
                startActivity(intent); //액티비티 이동.
            }
        });

        Button btn_safe = (Button)findViewById(R.id.btn_safe);
        btn_safe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SafeActivity.class);
                startActivity(intent); //액티비티 이동.
            }
        });

        Button btn_viewpager = (Button)findViewById(R.id.btn_viewpager);
        btn_viewpager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,vvv.class);
                startActivity(intent); //액티비티 이동.
            }
        });

        Button btn_board = (Button)findViewById(R.id.btn_board);
        btn_board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,PairReview.class);
                startActivity(intent); //액티비티 이동.
            }
        });



        Button btn_call = (Button)findViewById(R.id.btn_call);
        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:1339"));
                startActivity(intent); //액티비티 이동.
            }

        });

        drawerLayout.setDrawerListener(listener);
        drawerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        searchView = findViewById(R.id.sv_location);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if(location != null || !location.equals("")){
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        mapFragment.getMapAsync(this);


        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);


        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        clinics = (ArrayList<Clinic>)getIntent().getSerializableExtra("clinic");
        clinic_address = (ArrayList<Location>)getIntent().getSerializableExtra("clinic_addr");


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);
    }

    DrawerLayout.DrawerListener listener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };

    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;
        clusterManager = new ClusterManager<>(this, mMap);

        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
             //   Log.d(TAG, "Load");
                //LatLng latLng = new LatLng(37.564214, 127.001699);
               // CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
              //  mMap.animateCamera(cameraUpdate);

                for(int i = 0 ; i < clinics.size(); i++) {
                    MyItem clinicItem = new MyItem(clinic_address.get(i).getLatitude(), clinic_address.get(i).getLongitude(),
                            clinics.get(i).getName());
                    clusterManager.addItem(clinicItem);
                } // 병원 개수만큼 item 추가
            }
        });
        clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItem>() {
            @Override
            public boolean onClusterClick(Cluster<MyItem> cluster) {
                LatLng latLng = new LatLng(cluster.getPosition().latitude, cluster.getPosition().longitude);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                mMap.moveCamera(cameraUpdate);
                return false;
            }
        });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String marker_number = null;
                for (int i = 0; i < clinics.size(); i++) {
                    if (clinics.get(i).findIndex(marker.getTitle()) != null) {
                        marker_number = clinics.get(i).findIndex(marker.getTitle());
                        Log.d(TAG, "marker_number " + marker_number);
                    }
                } // marker title로 clinic을 검색하여 number 반환받아옴
                final int marker_ID_number = Integer.parseInt(marker_number);
                Log.d(TAG, "marker number = " + String.valueOf(marker_ID_number));
                Log.d(TAG, "marker clinic name = " + clinics.get(marker_ID_number).getName());
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("병원정보");
                builder.setMessage(
                        "이름 : " + clinics.get(marker_ID_number - 1).getName() +
                                "\n주소 : " + clinics.get(marker_ID_number - 1).getAddress() +
                                "\n병원전화번호 : " + clinics.get(marker_ID_number - 1).getPhoneNumber() +
                                "\n검체채취가능여부 : " + clinics.get(marker_ID_number - 1).getSample()
                );
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setNegativeButton("전화걸기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel" + clinics.get(Integer.parseInt(marker_ID_number)).getPhoneNumber())));
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });// 마커 클릭 시 Alert Dialog가 나오도록 설정


        MarkerOptions marker1 = new MarkerOptions();
        marker1.position(new LatLng(35.238389	, 128.656928))
                .title("창원-160")
                .snippet("의창구 원이대로 362 창원컨벤션센터");
        googleMap.addMarker(marker1).showInfoWindow();

        MarkerOptions marker2 = new MarkerOptions();
        marker2.position(new LatLng(35.221848, 128.677174))
                .title("창원-159")
                .snippet("성산구 중앙대로 69 호텔인터내셔널 3층 뷔페");
        googleMap.addMarker(marker2).showInfoWindow();

        MarkerOptions marker3 = new MarkerOptions();
        marker3.position(new LatLng(35.123908, 128.786099))
                .title("창원-158")
                .snippet("진해구 웅동로 89 운암정");
        googleMap.addMarker(marker3).showInfoWindow();

        MarkerOptions marker4 = new MarkerOptions();
        marker4.position(new LatLng(35.137582, 128.708671))
                .title("창원-157")
                .snippet("진해구 진해대로1033번길 5 담배집");
        googleMap.addMarker(marker4).showInfoWindow();

        MarkerOptions marker5 = new MarkerOptions();
        marker5.position(new LatLng(35.226891, 128.636281))
                .title("창원-156")
                .snippet("의창구 차상로30번길 2 취홍");
        googleMap.addMarker(marker5).showInfoWindow();

        MarkerOptions marker6 = new MarkerOptions();
        marker6.position(new LatLng(35.218965, 128.682995))
                .title("창원-155")
                .snippet("성산구 마디미로73번길 20 한스시");
        googleMap.addMarker(marker6).showInfoWindow();

        MarkerOptions marker7 = new MarkerOptions();
        marker7.position(new LatLng(35.225374, 128.680237))
                .title("창원-154")
                .snippet("성산구 중앙대로 123 롯데마트 창원중앙점\n" +
                        "지하1층 푸드코트");
        googleMap.addMarker(marker7).showInfoWindow();

        MarkerOptions marker8 = new MarkerOptions();
        marker8.position(new LatLng(35.246533, 128.670835))
                .title("창원-153")
                .snippet("의창구 소봉로 10 봉림동 산수정");
        googleMap.addMarker(marker8).showInfoWindow();

        MarkerOptions marker9 = new MarkerOptions();
        marker9.position(new LatLng(35.219171, 128.683204))
                .title("창원-152")
                .snippet("성산구 마디미동로 58 상남동 엠비어");
        googleMap.addMarker(marker9).showInfoWindow();

        MarkerOptions marker10 = new MarkerOptions();
        marker10.position(new LatLng(35.330661, 128.600593))
                .title("창원-151")
                .snippet("의창구 북면 천주로 868 북면마산설렁탕");
        googleMap.addMarker(marker10).showInfoWindow();

        MarkerOptions marker11 = new MarkerOptions();
        marker11.position(new LatLng(35.254704, 128.612211))
                .title("창원-150")
                .snippet("의창구 팔용로 425 팔용동 시골돼지장터국밥");
        googleMap.addMarker(marker11).showInfoWindow();

        MarkerOptions marker12 = new MarkerOptions();
        marker12.position(new LatLng(35.281414, 128.614027))
                .title("창원-149")
                .snippet("의창구 북면 천주로 240-10 북면 산수정");
        googleMap.addMarker(marker12).showInfoWindow();

        MarkerOptions marker13 = new MarkerOptions();
        marker13.position(new LatLng(35.222211, 128.683402))
                .title("창원-148")
                .snippet("성산구 마디미로 28 보리네포차");
        googleMap.addMarker(marker13).showInfoWindow();

        MarkerOptions marker14 = new MarkerOptions();
        marker14.position(new LatLng(35.222912, 128.686914))
                .title("창원-147")
                .snippet("성산구 마디미로3번길 16 김밥일번지 상남점");
        googleMap.addMarker(marker14).showInfoWindow();

        MarkerOptions marker15 = new MarkerOptions();
        marker15.position(new LatLng(35.207945, 128.640925))
                .title("창원-146")
                .snippet("성산구 월림로 65번길 6-7 신촌중앙식당");
        googleMap.addMarker(marker15).showInfoWindow();

        MarkerOptions marker16 = new MarkerOptions();
        marker16.position(new LatLng(35.198774, 128.708127))
                .title("창원-145")
                .snippet("성산구 삼정자로 11 창원경상대학교병원");
        googleMap.addMarker(marker16).showInfoWindow();

        MarkerOptions marker17 = new MarkerOptions();
        marker17.position(new LatLng(35.30294, 128.588631))
                .title("창원-144")
                .snippet("의창구 북면 감계로 259 감계힐스테이트3차아파트 근린생활시설동");
        googleMap.addMarker(marker17).showInfoWindow();

        MarkerOptions marker18 = new MarkerOptions();
        marker18.position(new LatLng(37.237931, 127.189525))
                .title("용인-1")
                .snippet("연세소망약국 경기도 용인시 처인구 역북동 424-1");
        googleMap.addMarker(marker18).showInfoWindow();

        MarkerOptions marker19 = new MarkerOptions();
        marker19.position(new LatLng(37.238948, 127.190278))
                .title("용인-2")
                .snippet("CU용인명지대역점 처인구 역북동 377-10번지 1층");
        googleMap.addMarker(marker19).showInfoWindow();

        MarkerOptions marker20 = new MarkerOptions();
        marker20.position(new LatLng(37.237400, 127.190265))
                .title("용인-3")
                .snippet("신성교회 경기도 용인시 처인구 역북동 425");
        googleMap.addMarker(marker20).showInfoWindow();

        MarkerOptions marker21 = new MarkerOptions();
        marker21.position(new LatLng(37.541176, 126.947102))
                .title("마포구-1")
                .snippet("맥도날드 공덕점 서울특별시 도화동 마포대로 53");
        googleMap.addMarker(marker21).showInfoWindow();

        MarkerOptions marker22 = new MarkerOptions();
        marker22.position(new LatLng( 37.538927, 126.945580))
                .title("마포구-2")
                .snippet("박재필치과 서울특별시 마포구 도화동 290-1");
        googleMap.addMarker(marker22).showInfoWindow();

        MarkerOptions marker23 = new MarkerOptions();
        marker23.position(new LatLng(37.539971, 126.945326))
                .title("마포구-3")
                .snippet("할리스 마포역점 서울특별시 마포구 도화동");
        googleMap.addMarker(marker23).showInfoWindow();

        mMap = googleMap;

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();



        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            startLocationUpdates(); // 3. 위치 업데이트 시작


        }else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                        ActivityCompat.requestPermissions( MainActivity.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }



        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 현재 오동작을 해서 주석처리

        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                Log.d( TAG, "onMapClick :");
            }
        });
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);

                currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());


                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                        + " 경도:" + String.valueOf(location.getLongitude());

                Log.d(TAG, "onLocationResult : " + markerSnippet);


                //현재 위치에 마커 생성하고 이동
                setCurrentLocation(location, markerTitle, markerSnippet);

                mCurrentLocatiion = location;
            }


        }

    };



    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);



            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED   ) {

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }


            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission())
                mMap.setMyLocationEnabled(true);

        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        if (checkPermission()) {

            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (mMap!=null)
                mMap.setMyLocationEnabled(true);

        }


    }


    @Override
    protected void onStop() {

        super.onStop();

        if (mFusedLocationClient != null) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }




    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {


        if (currentMarker != null) currentMarker.remove();


        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);

        final Vibrator mVib;
        mVib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        if(location.getLatitude() > 37.222108 && location.getLatitude() < 37.222292){
            if(location.getLongitude()> 127.187311 && location.getLongitude() < 127.187530) {
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        mVib.vibrate(10000);
                        Toast.makeText(getApplicationContext(),"확진자 방문지에 접근하였습니다.",Toast.LENGTH_LONG).show();
                    }
                },90000);
            }
        }


        currentMarker = mMap.addMarker(markerOptions); //수정

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        mMap.moveCamera(cameraUpdate);

    }


    public void setDefaultLocation() {


        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";


        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mMap.moveCamera(cameraUpdate);

    }


    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }

        return false;

    }



    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {


                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                }else {


                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();
                }
            }

        }
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");


                        needRequest = true;

                        return;
                    }
                }

                break;
        }
    }
}