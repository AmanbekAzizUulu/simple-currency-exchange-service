<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Обмен валют – REST API</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .button { display: inline-block; padding: 10px 20px; margin-top: 20px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 4px; }
    </style>
</head>
<body>
    <h1>Сервис обмена валют</h1>
    <p>Доступные методы REST API:</p>

    <table>
        <tr>
            <th>Метод</th>
            <th>Путь</th>
            <th>Описание</th>
        </tr>
        <tr>
            <td>GET</td>
            <td><code>/currencies</code></td>
            <td>Получить список всех валют</td>
        </tr>
        <tr>
            <td>GET</td>
            <td><code>/currency/{code}</code></td>
            <td>Получить конкретную валюту (например, /currency/EUR)</td>
        </tr>
        <tr>
            <td>POST</td>
            <td><code>/currencies</code></td>
            <td>Добавить новую валюту (поля формы: name, code, sign)</td>
        </tr>
        <tr>
            <td>GET</td>
            <td><code>/exchangeRates</code></td>
            <td>Получить список всех обменных курсов</td>
        </tr>
        <tr>
            <td>GET</td>
            <td><code>/exchangeRate/{pair}</code></td>
            <td>Получить курс для валютной пары (например, /exchangeRate/USDEUR)</td>
        </tr>
        <tr>
            <td>POST</td>
            <td><code>/exchangeRates</code></td>
            <td>Добавить новый обменный курс (поля: baseCurrencyCode, targetCurrencyCode, rate)</td>
        </tr>
        <tr>
            <td>PATCH</td>
            <td><code>/exchangeRate/{pair}</code></td>
            <td>Обновить курс валютной пары (поле формы: rate)</td>
        </tr>
        <tr>
            <td>GET</td>
            <td><code>/exchange?from=USD&amp;to=RUB&amp;amount=100</code></td>
            <td>Рассчитать конвертацию валюты</td>
        </tr>
    </table>

    <a href="${pageContext.request.contextPath}/" class="button">Перейти к тестовому фронтенду</a>

    <p style="margin-top: 30px; color: #888;">
        Контекст приложения: <strong>${pageContext.request.contextPath}</strong>
    </p>
</body>
</html>
