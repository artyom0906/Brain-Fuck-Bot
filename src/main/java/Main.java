import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;

public class Main {
    public static void main(String[] args) {
        if(args.length!=2){
            System.out.println("use: java -jar bot.jar <token> <name> <command>");
        }
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot(args[0], args[1], args[2]));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
