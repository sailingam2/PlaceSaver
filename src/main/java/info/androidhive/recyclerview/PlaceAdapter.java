package info.androidhive.recyclerview;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.MyViewHolder> implements OnDatabaseChangedListener{


    private PlaceDBHelper mDatabase;
    private Context context;
    private Place place;
    private LinearLayoutManager llm;
    @Override
    public void onNewDatabaseEntryAdded() {

    }

    @Override
    public void onDatabaseEntryRenamed() {

    }

    @Override
    public void onDatabaseImageChanged() {

    }

    protected class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        protected ImageView image;
        protected CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            image = (ImageView)view.findViewById(R.id.image);
            cardView = (CardView) view.findViewById(R.id.cardview);
        }
    }


    public PlaceAdapter(Context context, LinearLayoutManager linearLayoutManager) {

        super();
        this.context = context;
        this.mDatabase = new PlaceDBHelper(this.context);
        mDatabase.setOnDatabaseChangedListener(this);
        this.llm = linearLayoutManager;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_list_row, parent, false);

        this.context = parent.getContext();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final int pos = position;
       place = getItem(position);
        holder.name.setText(place.getName());
        Log.v("retrieve",place.getImagePath().toString());

        holder.image.setImageBitmap(loadImageFromStorage(place.getImagePath()));
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("position",""+pos);
                showBox(pos);
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.v("position",""+pos);
                Place place = getItem(pos);
                Log.v("cardpos",""+pos);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse(String.format("http://maps.google.com/maps?daddr=%f,%f",place.getLatitude(),place.getLongitude())));
                context.startActivity(intent);
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ArrayList<String> entrys = new ArrayList<String>();
                entrys.add(context.getString(R.string.dialog_file_rename));
                entrys.add(context.getString(R.string.dialog_file_delete));

                final CharSequence[] items = entrys.toArray(new CharSequence[entrys.size()]);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(context.getString(R.string.dialog_title_options));

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int option) {
                        if(option == 0){
                            renamePlace(pos);
                        }
                        if(option == 1){

                            deletePlace(pos);
                        }
                    }
                });

                builder.setCancelable(true);
                builder.setNegativeButton(context.getString(R.string.dialog_action_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();

return false;
            }
        });
    }

    private void showBox(int pos) {

        Place place = getItem(pos);
        AlertDialog.Builder ImageBoxBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_image_box,null);
        ImageView imageBox = (ImageView) view.findViewById(R.id.imageBox);
        Log.v("imagepath",place.getImagePath());
        imageBox.setImageBitmap(loadImageFromStorage(place.getImagePath()));
        ImageBoxBuilder.setTitle(place.getName());
        ImageBoxBuilder.setView(view);
        ImageBoxBuilder.setCancelable(true);
        ImageBoxBuilder.show();

    }

    private void deletePlace(int position) {

        mDatabase.removeItemWithId(getItem(position).getId());
        notifyItemRemoved(position);
    }

    private void renamePlace(final int position) {

        AlertDialog.Builder renamePlaceBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_rename_place,null);

        final EditText input = (EditText)view.findViewById(R.id.new_name);

        renamePlaceBuilder.setTitle(context.getString(R.string.dialog_title_rename));
        renamePlaceBuilder.setCancelable(true);
        renamePlaceBuilder.setPositiveButton(context.getString(R.string.dialog_action_ok),
                new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        try{
                            String newName = input.getText().toString();
                            String oldName = MainActivity.imageDirPath + "/" +place.getName()+place.getLatitude()+place.getLongitude();

                            mDatabase.renameItem(getItem(position),newName);
                            notifyItemChanged(position);
                           Log.v("directory",MainActivity.imageDirPath);
                        }catch (Exception e){
                            Log.v("rename","failed");
                        }
                    }
                });

        renamePlaceBuilder.setNegativeButton(context.getString(R.string.dialog_action_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        renamePlaceBuilder.setView(view);
        AlertDialog alert = renamePlaceBuilder.create();
        alert.show();

    }


    private Bitmap loadImageFromStorage(String imagePath)
    {

        try {
            File f=new File(MainActivity.imageDirPath, imagePath);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public int getItemCount() {
        return mDatabase.getCount();
    }

    public Place getItem(int position) {
        return mDatabase.getItemAt(position);
    }
}
