package ngodinhthang.apple.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.List;

import ngodinhthang.apple.R;
import ngodinhthang.apple.model.SanPhamMoi;

public class AsusAdapter extends RecyclerView.Adapter<AsusAdapter.MyViewHolder> {

    Context context;
    List<SanPhamMoi> array;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_asus,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SanPhamMoi sanPham = array.get(position);
        holder.tensp.setText(sanPham.getTensp());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.giasp.setText("Price: "+decimalFormat.format(Double.parseDouble(sanPham.getGiasp()))+"$");
        holder.mota.setText(sanPham.getMota());
        Glide.with(context).load(sanPham.getHinhanh()).into(holder.hinhanh);

    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public AsusAdapter(Context context, List<SanPhamMoi> array) {
        this.context = context;
        this.array = array;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tensp, giasp, mota;
        ImageView hinhanh;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tensp = itemView.findViewById(R.id.itemasus_ten);
            giasp = itemView.findViewById(R.id.itemasus_gia);
            mota = itemView.findViewById(R.id.itemasus_mota);
            hinhanh = itemView.findViewById(R.id.itemasus_image);
        }
    }
}
