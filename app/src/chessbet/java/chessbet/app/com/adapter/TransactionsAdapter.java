package chessbet.app.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import chessbet.app.com.R;
import chessbet.domain.Transaction;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {
    private List<Transaction> transactionList;
    private Context context;
    public TransactionsAdapter(@NonNull Context context, @NonNull List<Transaction> transactionList) {
        this.context = context;
        // Fetch Completed Transactions
        this.transactionList = new ArrayList<>();
        for (final Transaction transaction: transactionList) {
            if(transaction.isComplete()) {
                this.transactionList.add(transaction);
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.transaction_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.txtTransactionType.setText(transaction.getTransactionType());
        holder.txtDateCreated.setText(transaction.getDateCreated());
        holder.txtAmount.setText(String.format(Locale.ENGLISH,"%s %.2f","USD",transaction.getAmount()));
        holder.txtReferenceCode.setText(transaction.getRef());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtAmount;
        TextView txtDateCreated;
        TextView txtTransactionType;
        TextView txtReferenceCode;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.txtAmount = (TextView) itemView.findViewById(R.id.txtAmount);
            this.txtDateCreated = (TextView) itemView.findViewById(R.id.txtDateCreated);
            this.txtTransactionType = (TextView) itemView.findViewById(R.id.txtTransactionType);
            this.txtReferenceCode = (TextView) itemView.findViewById(R.id.txtReferenceCode);
        }
    }
}
