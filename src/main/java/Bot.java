import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bot extends TelegramLongPollingBot {
    private static Map<Long, Process> l = new HashMap<Long, Process>();

    private String token;
    private String name;
    private String command;
    public Bot(String token, String name, String command){
        this.command = command;
        this.name = name;
        this.token = token;
    }

    private void sendMessage(String message, Long chatId){
        SendMessage m = new SendMessage() // Create a SendMessage object with mandatory fields
                .setChatId(chatId)
                .setText(message);
        try {
            execute(m);
        } catch (TelegramApiException e) {
            //e.printStackTrace();
        }
    }

   public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if(message.isCommand()){
            if(message.getText().equals("/exit")){
                try {
                    l.get(message.getChatId()).destroy();
                    l.remove(message.getChatId());
                    sendMessage("stopped successful", message.getChatId());
                }catch (Exception e){
                    sendMessage("error stoping program", message.getChatId());
                }
            }else if(message.getText().equals("/brain")) {
                System.out.println(message);
                ProcessBuilder pb = new ProcessBuilder();
                pb.command(command);
                try {
                    Process p = pb.start();
                    l.put(message.getChatId(), p);
                    new Thread(() -> {
                        while (p.isAlive()) {
                            InputStream in = p.getInputStream();
                            byte[] data = new byte[1024];
                            try {
                                while (in.available() < 1) ;
                                in.read(data);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            sendMessage(new String(data), message.getChatId());
                        }
                    }).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else{
            try {
                System.out.println(message.getText());
                OutputStream o = l.get(message.getChatId()).getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(o));
                writer.write(message.getText()+"\n");
                writer.flush();
                //writer.close();
/*                o.write(message.getText().getBytes());
                o.flush();
                o.close();*/
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getBotUsername() {
        return name;//"test";
    }

    public String getBotToken() {
        return token;//"920401766:AAFN5XZ_eJUs6EI01z2ojfJvkBA5Dxw9FHg";
    }
}
