package nl.fontys.ivan.valentin.nightlife;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URLEncoder;
import java.util.ArrayList;

public class SuggestedPlace1 extends AppCompatActivity {

    //Fields
    int currentBitmap;
    int currentPlace;
    PlaceDetail first = null;
    PlaceDetail second = null;
    PlaceDetail third = null;
    ArrayList<Bitmap> bitMapsFirst = null;
    ArrayList<Bitmap> bitMapsSecond = null;
    ArrayList<Bitmap> bitMapsThird = null;
    ArrayList<PlaceDetail> places;
    TextView name;
    ImageView img;
    TextView openHours;
    TextView rating;
    TextView phone;
    Button back;
    Button forward;
    PlacesService placesService;
    Button previousBitmap;
    Button nextBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_place1);

        //Getting Info From Previous Activity
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");

        //Fields
        places = (ArrayList<PlaceDetail>) args.getSerializable("ARRAYLIST");
        name = findViewById(R.id.name_label);
        img = findViewById(R.id.image);
        openHours = findViewById(R.id.open_hours_label);
        rating = findViewById(R.id.rating_label);
        phone = findViewById(R.id.phone_label);
        back = findViewById(R.id.back_button);
        forward = findViewById(R.id.forward_button);
        placesService = new PlacesService();
        currentPlace = 0;
        previousBitmap = findViewById(R.id.backwards_bitmap);
        nextBitmap = findViewById(R.id.forward_bitmap);


        Thread newthread = new Thread(new Runnable() {
            public void run() {
                bitMapsFirst = null;
                bitMapsSecond = null;
                bitMapsThird = null;
                first = null;
                second = null;
                third = null;
                try {
                    if (places.size() == 3) {
                        first = places.get(0);
                        second = places.get(1);
                        third = places.get(2);

                        bitMapsFirst = placesService.getPhoto(places.get(0).photoReference);
                        bitMapsSecond = placesService.getPhoto(places.get(1).photoReference);
                        bitMapsThird = placesService.getPhoto(places.get(2).photoReference);
                    } else if (places.size() == 2) {
                        first = places.get(0);
                        second = places.get(1);
                        bitMapsFirst = placesService.getPhoto(places.get(0).photoReference);
                        bitMapsSecond = placesService.getPhoto(places.get(1).photoReference);

                    } else {
                        back.setVisibility(View.INVISIBLE);
                        forward.setVisibility(View.INVISIBLE);
                        first = places.get(0);
                        bitMapsFirst = placesService.getPhoto(places.get(0).photoReference);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // For Run
                final PlaceDetail firstt = first;
                final PlaceDetail secondd = second;
                final PlaceDetail thirdd = third;
                final ArrayList<Bitmap> bitMapsFirstt = bitMapsFirst;
                final ArrayList<Bitmap> bitMapsSecondd = bitMapsSecond;
                final ArrayList<Bitmap> bitMapsThirdd = bitMapsThird;

                SuggestedPlace1.this.runOnUiThread(new Runnable() {


                    @Override
                    public void run() {
                        if (places.size() == 3) {
                            back.setVisibility(View.VISIBLE);
                            forward.setVisibility(View.VISIBLE);
                        } else if (places.size() == 2) {
                            back.setVisibility(View.VISIBLE);
                            forward.setVisibility(View.VISIBLE);
                        } else {
                            back.setVisibility(View.INVISIBLE);
                            forward.setVisibility(View.INVISIBLE);
                        }
                        currentPlace = 0;
                        // Stuff that updates the UI
                        name.setText(firstt.name);
                        roundBitmap(bitMapsFirstt, 0);
                        if (firstt.alwaysOpen) {
                            openHours.setText("Open Hours: Always open");
                        } else {
                            openHours.setText("Open Hours: " + firstt.openTime + " - " + firstt.closeTime);
                        }
                        rating.setText("Rating: " + firstt.rating + " stars");
                        phone.setText("Phone: " + firstt.phoneNumber);
                        bitmapButtonCheck(currentPlace);

                    }
                });


            }
        });

        newthread.start();

    }

    public void updatePlace(int i) {
        name.setText(places.get(i).name);
        if (i == 0) {
            roundBitmap(bitMapsFirst, 0);
        } else if (i == 1) {
            roundBitmap(bitMapsSecond, 0);
        } else if (i == 2) {
            roundBitmap(bitMapsThird, 0);
        }
        currentBitmap = 0;
        if (places.get(i).alwaysOpen) openHours.setText("Open Hours: Always open");
        else
            openHours.setText("Open Hours: " + places.get(i).openTime + " - " + places.get(i).closeTime);
        rating.setText("Rating: " + places.get(i).rating + " stars");
        phone.setText("Phone: " + places.get(i).phoneNumber);
    }

    public void nextPlace() {

        if (places.size() == 1) ;
        else if (places.size() > 1 && places.size() < 4) {
            if (currentPlace + 1 > places.size() - 1) {
                this.updatePlace(0);
                currentPlace = 0;
                currentBitmap = 0;
            } else {
                currentPlace++;
                this.updatePlace(currentPlace);
                currentBitmap = 0;
            }
        }
        bitmapButtonCheck(currentPlace);
    }

    public void previousPlace() {

        if (places.size() == 1) ;
        else if (places.size() > 1 && places.size() < 4) {
            if (currentPlace - 1 < 0) {
                this.updatePlace(places.size() - 1);
                currentPlace = places.size() - 1;
                currentBitmap = 0;
            } else {
                currentPlace--;
                this.updatePlace(currentPlace);
                currentBitmap = 0;
            }
        }
        bitmapButtonCheck(currentPlace);
    }

    public void backButtonAction(View view) {
        previousPlace();
    }

    public void forwardButtonAction(View view) {
        nextPlace();
    }

    public void takeMeThere(View view) {
        String query = URLEncoder.encode(places.get(currentPlace).name);
        String extraQuery = URLEncoder.encode(places.get(currentPlace).placeId);
        String url = "https://www.google.com/maps/search/?api=1&query=" + query + "&query_place_id=" + extraQuery;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    public void nextBitmap() {
        if (currentPlace == 0) {
            if (currentBitmap + 1 > bitMapsFirst.size() - 1) currentBitmap = 0;
            else currentBitmap++;
            //Rounding and setting
            roundBitmap(bitMapsFirst, currentBitmap);
        } else if (currentPlace == 1) {
            if (currentBitmap + 1 > bitMapsSecond.size() - 1) currentBitmap = 0;
            else currentBitmap++;
            //Rounding and setting
            roundBitmap(bitMapsSecond, currentBitmap);
        } else if (currentPlace == 2) {
            if (currentBitmap + 1 > bitMapsThird.size() - 1) currentBitmap = 0;
            else currentBitmap++;
            //Rounding and setting
            roundBitmap(bitMapsThird, currentBitmap);
        }
    }

    public void previousBitmap() {
        if (currentPlace == 0) {
            if (currentBitmap - 1 < 0) currentBitmap = bitMapsFirst.size() - 1;
            else currentBitmap--;
            roundBitmap(bitMapsFirst, currentBitmap);

        } else if (currentPlace == 1) {
            if (currentBitmap - 1 < 1) currentBitmap = bitMapsSecond.size() - 1;
            else currentBitmap--;
            roundBitmap(bitMapsSecond, currentBitmap);
        } else if (currentPlace == 2) {
            if (currentBitmap - 1 < 1) currentBitmap = bitMapsThird.size() - 1;
            else currentBitmap--;
            roundBitmap(bitMapsThird, currentBitmap);
        }
    }

    public void nextBitmapButtonAction(View view) {
        nextBitmap();
    }

    public void previousBitmapButtonAction(View view) {
        previousBitmap();
    }

    public void roundBitmap(ArrayList<Bitmap> array, int i) {
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), array.get(i));
        final float roundPx = (float) array.get(i).getWidth() * 0.06f;
        roundedBitmapDrawable.setCornerRadius(roundPx);
        img.setImageDrawable(roundedBitmapDrawable);
    }

    public void bitmapButtonCheck(int i) {
        ArrayList<Bitmap> temp = null;
        if (i == 0) {
            temp = bitMapsFirst;
        } else if (i == 1) {
            temp = bitMapsSecond;
        } else if (i == 2) {
            temp = bitMapsThird;
        }
        if (temp != null) {
            if (temp.size() <= 1) {
                previousBitmap.setVisibility(View.INVISIBLE);
                nextBitmap.setVisibility(View.INVISIBLE);
            } else {
                previousBitmap.setVisibility(View.VISIBLE);
                nextBitmap.setVisibility(View.VISIBLE);
            }
        }
    }
}
