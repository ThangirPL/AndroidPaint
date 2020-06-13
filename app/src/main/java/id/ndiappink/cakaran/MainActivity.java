package id.ndiappink.cakaran;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.OutputStream;


public class MainActivity extends Activity implements OnClickListener, PopupMenu.OnMenuItemClickListener {
    EditText Et;
    Button zmianaTla, load;
    private ImageButton obecnyRysunek, drawBtn, nowy, gumka, zapis, cir_btn, sqr_btn, tri_btn;
    private DrawingView widokRysunku;
    ImageView viewImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        load = (Button) findViewById(R.id.load_btn);
        load.setOnClickListener(v -> {
            selectImage();
        } );
        viewImage = (ImageView) findViewById(R.id.viewImage);
        widokRysunku = (DrawingView) findViewById(R.id.drawing);
        drawBtn = (ImageButton) findViewById(R.id.draw_btn);
        nowy = (ImageButton) findViewById(R.id.new_btn);
        gumka = (ImageButton) findViewById(R.id.erase_btn);
        zapis = (ImageButton) findViewById(R.id.save_btn);
        LinearLayout paintLayout = (LinearLayout) findViewById(R.id.paint_colors);
        obecnyRysunek = (ImageButton) paintLayout.getChildAt(7);
        obecnyRysunek.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
        drawBtn.setOnClickListener(this);
        nowy.setOnClickListener(this);
        gumka.setOnClickListener(this);
        zapis.setOnClickListener(this);
        load.setOnClickListener(this);

        Et = (EditText) findViewById(R.id.editText);
        Et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (!(Et.getText().toString().isEmpty())) {
                    widokRysunku.setBrushSize(Float.valueOf(Et.getText().toString()));
                }
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

   public void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                MainActivity.this.startActivityForResult(intent, 1);
            } else if (options[item].equals("Choose from Gallery")) {
                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                   intent.setType("image/*");
                   startActivityForResult(Intent.createChooser(intent, "Select File"),2);
                //Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //MainActivity.this.startActivityForResult(intent, 2);
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                   photoPickerIntent.setType("image/*");
                   startActivityForResult(photoPickerIntent, 2);
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    public void paintClicked(View view) {
        if (view != obecnyRysunek) {
            ImageButton imgView = (ImageButton) view;
            String color = view.getTag().toString();

            zmianaTla = findViewById(R.id.button);

            zmianaTla.setOnClickListener(v -> {
                //Toast.makeText(getApplicationContext(), "Switch", Toast.LENGTH_LONG).show();
                View someView = findViewById(R.id.drawing);
                someView.setBackgroundColor(Color.parseColor(color));
            });




            widokRysunku.setColor(color);
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            obecnyRysunek.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            obecnyRysunek = (ImageButton) view;
        }
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                widokRysunku.tri();
                Toast.makeText(this, "Wybrałeś trójkąt", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item2:
                widokRysunku.rect();
                Toast.makeText(this, "Wybrałeś Kwadrat", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item3:
                widokRysunku.circ();
                Toast.makeText(this, "Wybrałeś Kółko", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item4:
                widokRysunku.star();
                Toast.makeText(this, "Wybrałeś Gwiazdke", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item5:
                widokRysunku.heart();
                Toast.makeText(this, "Wybrałeś Serce", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item6:

                AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
                saveDialog.setTitle("Wprowadź tekst");
                saveDialog.setMessage("Poniżej podaj tekst do wprowadzenia");

                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                saveDialog.setView(input);

                saveDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = "";
                        m_Text = input.getText().toString();
                        widokRysunku.Text(m_Text);


                    }
                });
                saveDialog.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                saveDialog.show();
            default:
                return false;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.load_btn) {
            selectImage();
        }
        if (v.getId() == R.id.draw_btn) {
            widokRysunku.setupDrawing();
        }
        if (v.getId() == R.id.erase_btn) {
            widokRysunku.setErase(true);
            widokRysunku.setBrushSize(widokRysunku.getLastBrushSize());
        }
        if (v.getId() == R.id.new_btn) {
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("Nowy Rysunek");
            newDialog.setMessage("Czy chcesz utworzyć nowy rysunek ( stracisz niezapisany postęp )");
            newDialog.setPositiveButton("TAK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    widokRysunku.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("NIE", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            newDialog.show();
        }
        if (v.getId() == R.id.save_btn) {


            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Zapis Rysuneku");
            saveDialog.setMessage("Czy chcesz zapisać rysunek do galerii telefonu?");

            final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            saveDialog.setView(input);

            saveDialog.setPositiveButton("TAK", (dialog, which) -> {
                String m_Text = "";
                m_Text = input.getText().toString();
                widokRysunku.setDrawingCacheEnabled(true);
                String imgSaved = MediaStore.Images.Media.insertImage(
                        getContentResolver(), widokRysunku.getDrawingCache(),
                        m_Text, "RYSUNEK");
                if (imgSaved != null) {
                    Toast savedToast = Toast.makeText(getApplicationContext(),
                            "Rysunek zapisany w Galerii!\n" + "Szukaj nazwy: " + m_Text + ".jpg", Toast.LENGTH_SHORT);
                    savedToast.show();
                } else {
                    Toast unsavedToast = Toast.makeText(getApplicationContext(),
                            "Oops! Coś poszło nie tak. Rysunek nie został zapisany.", Toast.LENGTH_SHORT);
                    unsavedToast.show();
                }
                widokRysunku.destroyDrawingCache();

            });
            saveDialog.setNegativeButton("NIE", (dialog, which) -> dialog.cancel());
            saveDialog.show();
        }
    }
    @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 70, 70, true);
                    viewImage.setImageBitmap(bitmap);
                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null ;
                    File file = new File(path, System.currentTimeMillis() + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {
                Uri selectedImageUri = data.getData();
                String p=selectedImageUri.getPath();

                String tempPath = data.getDataString();
                Bitmap bitmap;
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmap = BitmapFactory.decodeFile(tempPath, bitmapOptions);
                viewImage.setImageBitmap(bitmap);
                //Uri selectedImage = data.getData();
                //String[] filePath = { MediaStore.Images.Media.DATA };
                //Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                //c.moveToFirst();
                //int columnIndex = c.getColumnIndex(filePath[0]);
                //String picturePath = c.getString(columnIndex);
                //c.close();
                //Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                //int w = Log.w("path of image from gallery......******************.........", picturePath + "");
                //viewImage.setImageBitmap( thumbnail);
            }
        }
    }





}