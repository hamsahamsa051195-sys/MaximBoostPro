package com.maximboostpro;

import android.app.*;
import android.os.*;
import android.content.*;
import android.net.*;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    LinearLayout main;
    boolean focusMode = false;
    int navy = Color.rgb(18, 35, 64), blue = Color.rgb(32, 112, 235);
    int light = Color.rgb(244, 247, 251);

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        showHome();
    }

    TextView text(String s, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s); t.setTextSize(size); t.setTextColor(color);
        if (bold) t.setTypeface(null, android.graphics.Typeface.BOLD);
        t.setPadding(0, 4, 0, 4);
        return t;
    }

    void showHome() {
        ScrollView scroll = new ScrollView(this);
        main = new LinearLayout(this); main.setOrientation(LinearLayout.VERTICAL);
        main.setPadding(20, 20, 20, 24); main.setBackgroundColor(light);
        scroll.addView(main);
        setContentView(scroll);

        TextView header = text("MAXIM BOOST PRO", 23, Color.WHITE, true);
        header.setPadding(18, 22, 18, 8);
        TextView sub = text("Asisten produktivitas driver • versi 1.0", 14, Color.WHITE, false);
        sub.setPadding(18, 0, 18, 20);
        LinearLayout banner = new LinearLayout(this); banner.setOrientation(1); banner.setBackgroundColor(navy);
        banner.addView(header); banner.addView(sub); main.addView(banner);

        TextView net = text("Status koneksi: " + connectionStatus(), 16, navy, true);
        net.setPadding(4, 18, 4, 10); main.addView(net);

        card("🚀 TURBO TOOLS", "Akses cepat Wi-Fi/data dan tips mengurangi lag.",
             "Buka pengaturan jaringan", () -> openSettings(Settings.ACTION_WIRELESS_SETTINGS));
        card("🕒 JAM POTENSIAL", "Panduan awal: pagi 06.00–09.00 • makan siang 11.00–14.00 • sore/malam 17.00–21.00. Ini perkiraan umum, bukan data resmi Maxim. Catat hasilmu untuk mengetahui pola lokal.",
             "Catat waktu order", () -> recordOrder());
        card("📶 JARINGAN", "Cek koneksi saat ini dan buka pengaturan jaringan Android. Aplikasi tidak dapat meningkatkan daya antena atau mengubah jaringan operator secara ajaib.",
             "Pengaturan koneksi", () -> openSettings(Settings.ACTION_WIRELESS_SETTINGS));
        card("📍 AREA POTENSIAL", "Cari pusat kuliner, mall, pasar, terminal, atau area perkantoran di sekitar lokasi kamu lewat Maps.",
             "Cari tempat ramai di Maps", () -> mapsSearch("tempat ramai restoran mall pasar"));
        card("🎯 FOKUS ORDER", focusMode ? "Mode fokus AKTIF. Layar dijaga tetap menyala saat aplikasi terbuka." : "Kurangi gangguan. Mode ini menjaga layar tetap menyala saat aplikasi dibuka.",
             focusMode ? "Matikan mode fokus" : "Aktifkan mode fokus", () -> toggleFocus());
        card("📝 CATATAN ORDER", "Catatan sederhana untuk membantu kamu melihat jam dan area mana yang lebih produktif.",
             "Tambah catatan order", () -> recordOrder());

        TextView disclaimer = text("Catatan: aplikasi pendamping ini tidak terhubung ke server Maxim, tidak menjamin order, tidak auto-accept, dan tidak memanipulasi GPS atau sistem order.", 12, Color.DKGRAY, false);
        disclaimer.setPadding(8, 14, 8, 6); main.addView(disclaimer);
    }

    void card(String title, String desc, String button, Runnable action) {
        LinearLayout box = new LinearLayout(this); box.setOrientation(1);
        box.setPadding(16, 14, 16, 14); box.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2); p.setMargins(0, 12, 0, 0);
        box.setElevation(2); main.addView(box, p);
        box.addView(text(title, 17, navy, true));
        TextView d = text(desc, 14, Color.DKGRAY, false); d.setPadding(0, 5, 0, 10); box.addView(d);
        Button b = new Button(this); b.setText(button); b.setOnClickListener(v -> action.run()); box.addView(b);
    }

    String connectionStatus() {
        try {
            android.net.ConnectivityManager cm = (android.net.ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
            android.net.Network n = cm.getActiveNetwork();
            if (n == null) return "Tidak terhubung";
            android.net.NetworkCapabilities c = cm.getNetworkCapabilities(n);
            if (c == null) return "Tidak diketahui";
            if (c.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)) return "Wi-Fi aktif";
            if (c.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)) return "Data seluler aktif";
            return "Terhubung";
        } catch (Exception e) { return "Tidak dapat membaca status"; }
    }

    void openSettings(String action) {
        try { startActivity(new Intent(action)); }
        catch (Exception e) { toast("Pengaturan tidak tersedia di perangkat ini."); }
    }

    void mapsSearch(String query) {
        Uri uri = Uri.parse("geo:0,0?q=" + Uri.encode(query));
        try { startActivity(new Intent(Intent.ACTION_VIEW, uri)); }
        catch (Exception e) { toast("Aplikasi peta tidak ditemukan."); }
    }

    void toggleFocus() {
        focusMode = !focusMode;
        if (focusMode) getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        else getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        showHome();
        toast(focusMode ? "Mode fokus aktif" : "Mode fokus dimatikan");
    }

    void recordOrder() {
        EditText input = new EditText(this);
        input.setHint("Contoh: 08:15 - area pasar - dapat order");
        new AlertDialog.Builder(this).setTitle("Catat aktivitas order")
            .setMessage("Waktu: " + new SimpleDateFormat("HH:mm, EEEE dd MMM", Locale.getDefault()).format(new Date()))
            .setView(input).setPositiveButton("Simpan", (d, w) -> {
                String note = input.getText().toString().trim();
                if (!note.isEmpty()) {
                    String old = getPreferences(0).getString("notes", "");
                    String updated = old + "\n• " + new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(new Date()) + " — " + note;
                    getPreferences(0).edit().putString("notes", updated).apply();
                    showNotes();
                } else toast("Catatan kosong.");
            }).setNegativeButton("Batal", null).show();
    }

    void showNotes() {
        String notes = getPreferences(0).getString("notes", "Belum ada catatan order.");
        new AlertDialog.Builder(this).setTitle("Riwayat catatan").setMessage(notes)
            .setPositiveButton("OK", null).setNeutralButton("Hapus catatan", (d,w) -> {
                getPreferences(0).edit().remove("notes").apply(); toast("Catatan dihapus.");
            }).show();
    }

    void toast(String s) { Toast.makeText(this, s, Toast.LENGTH_SHORT).show(); }
}
