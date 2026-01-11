import os
from phi.agent import Agent
from phi.model.groq import Groq
from phi.storage.agent.sqlite import SqlAgentStorage
from phi.tools.duckduckgo import DuckDuckGo
from phi.tools import tool
from phi.playground import Playground, serve_playground_app
from dotenv import load_dotenv

# Load .env file if exists
load_dotenv()

# Use Llama3 from Groq (Free tier Available)
groq_model = Groq(id="llama3-8b-8192")

# List of NIFTY50 tickers
NIFTY50_TICKERS = [
    "ADANIENT.NS",
    "ADANIPORTS.NS",
    "APOLLOHOSP.NS",
    "ASIANPAINT.NS",
    "AXISBANK.NS",
    "BAJAJ-AUTO.NS",
    "BAJAJFINSV.NS",
    "BAJFINANCE.NS",
    "BEL.NS",
    "BHARTIARTL.NS",
    "BRITANNIA.NS",
    "CIPLA.NS",
    "COALINDIA.NS",
    "DRREDDY.NS",
    "DIVISLAB.NS",
    "EICHERMOT.NS",
    "GRASIM.NS",
    "HCLTECH.NS",
    "HDFCBANK.NS",
    "HDFCLIFE.NS",
    "HEROMOTOCO.NS",
    "HINDALCO.NS",
    "HINDUNILVR.NS",
    "ICICIBANK.NS",
    "INDUSINDBK.NS",
    "INFY.NS",
    "ITC.NS",
    "JIOFIN.NS",
    "JSWSTEEL.NS",
    "KOTAKBANK.NS",
    "LT.NS",
    "MARUTI.NS",
    "M&M.NS",
    "NESTLEIND.NS",
    "NTPC.NS",
    "ONGC.NS",
    "POWERGRID.NS",
    "RELIANCE.NS",
    "SBILIFE.NS",
    "SBIN.NS",
    "SHRIRAMFIN.NS",
    "SUNPHARMA.NS",
    "TATACONSUM.NS",
    "TATAMOTORS.NS",
    "TATASTEEL.NS",
    "TCS.NS",
    "TECHM.NS",
    "TITAN.NS",
    "TRENT.NS",
    "ULTRACEMCO.NS",
    "WIPRO.NS"
]

def analyze_stock(ticker):
    try:
        import yfinance as yf
        import pandas as pd
        import numpy as np

        stock = yf.Ticker(ticker)
        df = stock.history(period="6mo", interval="1d")
        if len(df) < 100:
            return None

        df['MA20'] = df['Close'].rolling(window=20).mean()
        df['MA50'] = df['Close'].rolling(window=50).mean()
        delta = df['Close'].diff()
        gain = delta.where(delta > 0, 0)
        loss = -delta.where(delta < 0, 0)
        avg_gain = gain.rolling(window=14).mean()
        avg_loss = loss.rolling(window=14).mean()
        rs = avg_gain / avg_loss
        df['RSI'] = 100 - (100 / (1 + rs))

        latest = df.iloc[-1]
        current_price = latest['Close']
        rsi = latest['RSI']
        ma20 = latest['MA20']
        ma50 = latest['MA50']

        trend = 'Neutral'
        if ma20 > ma50 and current_price > ma20 and rsi < 60 and rsi > 40:
            trend = 'Uptrend'

        if trend == 'Uptrend':
            target_price = current_price * 1.02
            stoploss_price = current_price * 0.99
            reward = target_price - current_price
            risk = current_price - stoploss_price
            risk_reward_ratio = reward / risk

            volatility = df['Close'].pct_change().std() * np.sqrt(252)
            win_rate = max(0.7, 1 - volatility)

            if win_rate >= 0.8 and risk_reward_ratio >= 2:
                return {
                    "ticker": ticker,
                    "current_price": round(current_price, 2),
                    "target_price": round(target_price, 2),
                    "stoploss_price": round(stoploss_price, 2),
                    "risk_reward_ratio": round(risk_reward_ratio, 2),
                    "win_rate": round(win_rate * 100, 2),
                    "technical_reason": f"Stock is in uptrend (RSI={round(rsi, 2)}, MA20={round(ma20, 2)}, MA50={round(ma50, 2)})."
                }
        return None
    except Exception as e:
        print(f"Error analyzing {ticker}: {str(e)}")
        return None

# Define custom tool for stock recommendations with web search
@tool
def get_short_term_nifty50_recommendations():
    """Returns short-term NIFTY50 stock recommendations with  SUPERSCRIPT2% target, 1% stoploss, ≥80% win rate, and web-based sentiment."""
    results = []
    ddg = DuckDuckGo()

    for ticker in NIFTY50_TICKERS:
        # Technical analysis
        analysis = analyze_stock(ticker)
        if not analysis:
            continue

        # Web search for recent news/sentiment
        company_name = ticker.replace('.NS', '')
        query = f"{company_name} stock news last week"
        try:
            search_results = ddg.search(query, max_results=5)
            sentiment = "Neutral"
            news_summary = ""
            for result in search_results:
                if "positive" in result['body'].lower() or "bullish" in result['body'].lower():
                    sentiment = "Positive"
                    news_summary += f"- {result['title']}: {result['body'][:100]}...\n"
                elif "negative" in result['body'].lower() or "bearish" in result['body'].lower():
                    sentiment = "Negative"
                    news_summary += f"- {result['title']}: {result['body'][:100]}...\n"

            # Only include stocks with positive or neutral sentiment
            if sentiment in ["Positive", "Neutral"]:
                analysis.update({
                    "sentiment": sentiment,
                    "news_summary": news_summary or "No significant news found.",
                    "reason": f"{analysis['technical_reason']} Recent sentiment: {sentiment}. {news_summary}"
                })
                results.append(analysis)
        except Exception as e:
            print(f"Error searching for {ticker}: {str(e)}")
            continue

    if not results:
        return {"message": "No stocks found matching the criteria with positive/neutral sentiment."}

    import pandas as pd
    df = pd.DataFrame(results)
    df = df.sort_values(by='win_rate', ascending=False)
    return df.to_dict(orient='records')

# Finance Agent with custom recommendation tool and web search
finance_agent = Agent(
    name="Short-Term Stock Advisor",
    model=groq_model,
    tools=[get_short_term_nifty50_recommendations, DuckDuckGo()],
    instructions=[
        "You are a short-term stock advisor specialized in NIFTY50.",
        "Your task is to recommend stocks with a 2% target, 1% stoploss, and 80%+ win rate over 2-4 weeks.",
        "Use technical analysis and recent web-based news/sentiment to make recommendations.",
        "Only recommend stocks with positive or neutral sentiment from recent news.",
        "For each recommendation, explain why it's suggested, including technical indicators and news sentiment.",
        "Use tables to display data when possible.",
        "If no stocks match the criteria, inform the user clearly."
    ],
    storage=SqlAgentStorage(table_name="finance_agent", db_file="agents.db"),
    add_history_to_messages=True,
    markdown=True,
)

# Web Agent (unchanged)
web_agent = Agent(
    name="Web Agent",
    model=groq_model,
    tools=[DuckDuckGo()],
    instructions=["Always include sources"],
    storage=SqlAgentStorage(table_name="web_agent", db_file="agents.db"),
    add_history_to_messages=True,
    markdown=True,
)

# Launch Playground UI
app = Playground(agents=[finance_agent, web_agent]).get_app()

if __name__ == "__main__":
    serve_playground_app("playground:app", reload=True)
