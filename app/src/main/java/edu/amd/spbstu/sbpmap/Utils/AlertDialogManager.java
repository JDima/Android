package edu.amd.spbstu.sbpmap.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;

import edu.amd.spbstu.sbpmap.MainActivity;
import edu.amd.spbstu.sbpmap.Map.WebPlaceFinder;
import edu.amd.spbstu.sbpmap.R;

import java.util.ArrayList;


public class AlertDialogManager {

    public static final String HOTEL = "Отель";
    public static final String HOSTEL = "Хостел";
    public static final String MINI_HOTEL = "Миниотель";
    public static final String LANDMARK = "Достопримечательность";
    public static final String BRIDGE = "Мост";
    public static final String PARK = "Парк";
    public static final String MONUMENT = "Монумент";
    public static final String RESTAURANT = "Ресторан";

    public static final String[] RU_VENUES = {RESTAURANT, HOTEL, LANDMARK, HOSTEL, MINI_HOTEL, MONUMENT, BRIDGE, PARK};

	@SuppressWarnings("deprecation")
    public static AlertDialog alertDialog(Context context, String title, String message, int icon) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        alertDialog.setTitle(title);


        alertDialog.setMessage(message);
        alertDialog.setIcon(icon);

        alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return alertDialog;
    }


	public static void showAlertDialog(Context context, String title, String message, int icon) {
         AlertDialog alertDialog = alertDialog(context, title, message, icon);
         //alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
         alertDialog.show();
     }

    public void showSelectCategoryDialog(final Context context, final WebPlaceFinder fp, final double lat, final double lng, final LatLngBounds latLngBounds) {
        final AlertDialog dialog;
        Log.d("Java log", "showSelectCategoryDialog()");
        final String notSelected, titleSelectCategory, error, search, cancel;

        notSelected = MainActivity.isEnglish ? "You did not select categories!" : "Вы не выбрали категории!";
        titleSelectCategory = MainActivity.isEnglish ? "Select categories" : "Выберите категории!";
        error = MainActivity.isEnglish ? "Error!" : "Ошибка!";
        search = MainActivity.isEnglish ? "Search" : "Поиск";
        cancel = MainActivity.isEnglish ? "Cancel" : "Отменить";

        final ArrayList<Integer> seletedItems = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titleSelectCategory);

        builder.setMultiChoiceItems(MainActivity.isEnglish ? WebPlaceFinder.VENUES : RU_VENUES, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected,
                                        boolean isChecked) {
                        if (isChecked) {
                            seletedItems.add(indexSelected);
                        } else if (seletedItems.contains(indexSelected)) {
                            seletedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                })
                .setPositiveButton(search, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                    }
                })
                .setNegativeButton(cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                    }
                });


        dialog = builder.create();
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (seletedItems.isEmpty()) {
                    AlertDialogManager.showAlertDialog(context, error,
                            notSelected, R.drawable.fail);
                } else {
                    fp.removeAll();
                    fp.searchPlaces(lat, lng, seletedItems, latLngBounds);

                    dialog.dismiss();
                }
            }
        });

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
    }

    public void connectionError(Context context) {
        String title, msg;
        if (MainActivity.isEnglish) {
            title = "Internet Connection Error";
            msg = "Please connect to Internet!";
        } else {
            title = "Ошибка интернет соединения";
            msg = "Подключитесь к интернету!";
        }
        AlertDialogManager.showAlertDialog(context, title,
                msg, R.drawable.fail);
    }
}
