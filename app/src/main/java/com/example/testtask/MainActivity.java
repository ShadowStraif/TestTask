package com.example.testtask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;

import com.example.testtask.Adapter.ConnectionAdapter;
import com.example.testtask.Models.ConnectionModel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView connectionRecycler;
    private ConnectionAdapter connectAdapter;
    private List<ConnectionModel> connectList;
    private ServerSocket serverSocket;
    public static final int PORT = 6000;
    Thread serverThread = null;
    Handler updateConversationHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        connectionRecycler = findViewById(R.id.connectionRecyclerView);
        connectionRecycler.setLayoutManager(new LinearLayoutManager(this));
        connectAdapter = new ConnectionAdapter(this);
        connectionRecycler.setAdapter(connectAdapter);

        connectList = new ArrayList<>();
        ConnectionModel connect = new ConnectionModel();

        connectAdapter.setConnection(connectList);
        connectAdapter.notifyDataSetChanged();


        updateConversationHandler = new Handler();
        this.serverThread = new Thread(new Server());
        this.serverThread.start();

    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class Server implements Runnable {
        public void run() {

            Socket socket = null;
            try {
                serverSocket = new ServerSocket(PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    socket = serverSocket.accept();
                    ClientCommunication commThread = new ClientCommunication(socket);
                    new Thread(commThread).start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private MainActivity activity;

    class ClientCommunication implements Runnable {

        private Socket clientSocket;
        private BufferedReader reader;
        private BufferedWriter writer;
        private ConnectionAdapter connectAdapter;
        private List<ConnectionModel> connectList;
        private boolean flag;
        public ClientCommunication(Socket clientSocket) {

            this.clientSocket = clientSocket;
            try {
                this.reader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
                this.writer = new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            connectList = new ArrayList<>();
            connectAdapter = new ConnectionAdapter(activity);


            flag = true;

            // while (!Thread.currentThread().isInterrupted()) {
            if (!clientSocket.isClosed()) {
                try {
                    String ip = String.valueOf(clientSocket.getLocalAddress());
                    String port = String.valueOf(clientSocket.getPort());
                    String read = reader.readLine();

                    writer.write(read);
                    writer.newLine();
                    writer.flush();

                    updateConversationHandler.post(new updateUIThread(ip, port, read,flag));
                    writer.close();
                    flag = false;
                    updateConversationHandler.post(new updateUIThread(ip, port, read,flag));
                    flag = true;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        //}

        }
    }

    class updateUIThread implements Runnable {

        private String foundIP;
        private String foundPort;
        private String message;
        private boolean flag;
        public updateUIThread(String strip,String strP,String strM,boolean flag) {
            this.foundIP = strip;
            this.foundPort = strP;
            this.message = strM;
            this.flag = flag;
        }

        @Override
        public void run() {
            if(!flag) {
                ConnectionModel connect = new ConnectionModel();
                connect.setIpAdress(foundIP);
                connect.setPortNum(foundPort);
                connect.setMessage(message);
                connectList.add(connect);

                connectAdapter.setConnection(connectList);
                connectAdapter.notifyDataSetChanged();
            }
            else
            {
                connectAdapter.deleteConection();
                connectAdapter.notifyDataSetChanged();
            }

        }

    }

}