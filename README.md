# Түйіндеме Бот 📝

Telegram топтарындағы хабарламаларды жинап, олардың қысқаша мазмұнын жасайтын бот.

## Мүмкіндіктері 🚀

- `/summary` - Соңғы хабарламалардың қысқаша мазмұнын жасау
- `/credits` - Қолданылған кредиттерді көру

## Технологиялар 💻

- Java 21
- Spring Boot 3.4
- Spring AI (OpenAI интеграциясы)
- SQLite
- Telegram Bot API

## Орнату 🛠

### Қажетті құралдар

- Java 21 немесе жоғары
- Gradle
- SQLite

### Қоршаған орта айнымалылары

`.env` файлын жасап, келесі айнымалыларды қосыңыз:

```properties
# Telegram Bot параметрлері
BOT_TOKEN=your_bot_token
BOT_USERNAME=your_bot_username
AUTHORIZED_USERS=user1,user2
AUTHORIZED_CHAT_GROUP_ID=your_group_id
DEVELOPER_CHAT_ID=your_developer_chat_id

# OpenAI параметрлері
OPENAI_API_KEY=your_openai_api_key
OPENAI_INTELLIGENT_MODEL=gpt-4
OPENAI_FAST_MODEL=gpt-3.5-turbo

# Қосымша параметрлер
MAX_MESSAGES=50
SUMMARY_COMMAND_MINUTES=1
```

### Жобаны іске қосу

```bash
# Жобаны клондау
git clone https://github.com/your-username/tuindeme-bot.git
cd tuindeme-bot

# Жобаны құрастыру
./gradlew build

# Жобаны іске қосу
./gradlew bootRun
```

## Қауіпсіздік 🔒

- Тек рұқсат етілген топтардан хабарламалар қабылданады
- Командаларды тек рұқсат етілген қолданушылар орындай алады
- Қателер туралы әзірлеушіге хабарланады

## Жұмыс принципі ⚙️

1. Бот топтағы хабарламаларды жинайды (максимум 50)
2. `/summary` командасы арқылы GPT моделін қолданып қысқаша мазмұн жасалады
3. Әр қолданғанда кредит есептеледі
4. `/credits` командасы арқылы жалпы шығындарды көруге болады

## Шектеулер ⚠️

- `/summary` командасын 1 минутта бір рет қана қолдануға болады
- Максималды хабарлама саны: 50
- Тек мәтіндік хабарламалар өңделеді

## Әзірлеушілер үшін 👩‍💻👨‍💻

### Жобаның құрылымы

```
src/main/java/dev/skrra/tuindeme/bot/
├── config/          # Конфигурация кластары
├── model/           # Деректер модельдері
├── repository/      # Дерекқор репозиторийлері
├── service/         # Бизнес логика
└── util/           # Көмекші кластар
```

### Қосымша функционал қосу

1. Жаңа команда қосу үшін `CommandProcessor` класын өңдеңіз
2. Жаңа қызмет қосу үшін тиісті интерфейс пен имплементация жасаңыз
3. Жаңа конфигурация параметрлері үшін тиісті properties класын өңдеңіз

## Лицензия 📄

MIT License
