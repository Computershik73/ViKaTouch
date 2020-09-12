package ru.zorin.picfont;

import java.io.IOException;
import java.io.Reader;

public class Parser
{
  Reader reader;
  String ignored;
  String commentStart;
  String whiteSpaces;
  String keyValueDelimiters;
  String fieldDelimiters;
  
  public Parser(Reader reader)
  {
    this.reader = reader;
    this.ignored = "\r";
    this.commentStart = "#;";
    this.whiteSpaces = " \t";
    this.keyValueDelimiters = "=";
    this.fieldDelimiters = ";";
  }
  
  public int readPair(StringBuffer key, StringBuffer value)
  {
    int ret = 0;
    
    int state = 0;
    
    key.delete(0, key.length());
    value.delete(0, value.length());
    try
    {
      int ich;
      while ((ich = this.reader.read()) != -1)
      {
        char ch = (char)ich;
        if (!isIgnored(ch))
        {
          if ((state != 0) && (state != 5) && (ch == '\n')) {
            break;
          }
          switch (state)
          {
          case 0: 
            if ((ch != '\n') && 
            
              (!isWhiteSpace(ch))) {
              if (isKeyValueDelimiter(ch))
              {
                state = 3;
              }
              else if (isCommentStart(ch))
              {
                state = 5;
              }
              else
              {
                key.append(ch);
                state = 1;
              }
            }
            break;
          case 1: 
            if (isWhiteSpace(ch)) {
              state = 2;
            } else if (isKeyValueDelimiter(ch)) {
              state = 3;
            } else {
              key.append(ch);
            }
            break;
          case 2: 
            if (isKeyValueDelimiter(ch)) {
              state = 3;
            }
            break;
          case 3: 
            if (!isWhiteSpace(ch))
            {
              value.append(ch);
              state = 4;
            }
            break;
          case 4: 
            value.append(ch);
            break;
          case 5: 
            if (ch == '\n') {
              state = 0;
            }
            break;
          }
        }
      }
      if (ich == -1) {
        ret = -1;
      }
      int last = value.length() - 1;
      while ((last >= 0) && 
        (isWhiteSpace(value.charAt(last)))) {
        value.setLength(last--);
      }
    }
    catch (IOException ex)
    {
      ret = -2;
    }
    return ret;
  }
  
  public int readFields(StringBuffer[] fields)
  {
    int ret = 0;
    
    int state = 0;
    int max = fields.length;
    int cur = max - 1;
    while (cur >= 0)
    {
      if (fields[cur] == null) {
        fields[cur] = new StringBuffer();
      }
      fields[cur].delete(0, fields[cur].length());
      cur--;
    }
    cur = -1;
    try
    {
      int ich;
      while ((ich = this.reader.read()) != -1)
      {
        char ch = (char)ich;
        if (!isIgnored(ch)) {
          if (ch == '\n')
          {
            if (cur != -1) {
              break;
            }
            state = 0;
          }
          else
          {
            switch (state)
            {
            case 0: 
              if (!isWhiteSpace(ch)) {
                if (isCommentStart(ch))
                {
                  state = 5;
                }
                else
                {
                  cur++;
                  if (cur < max)
                  {
                    fields[cur].append(ch);
                    state = 1;
                  }
                  else
                  {
                    state = 5;
                  }
                }
              }
              break;
            case 1: 
              if ((isWhiteSpace(ch)) || (isFieldDelimiter(ch))) {
                state = 2;
              } else if (isCommentStart(ch)) {
                state = 5;
              } else {
                fields[cur].append(ch);
              }
              break;
            case 2: 
              if ((!isWhiteSpace(ch)) && (!isFieldDelimiter(ch))) {
                if (isCommentStart(ch))
                {
                  state = 5;
                }
                else
                {
                  cur++;
                  if (cur < max)
                  {
                    fields[cur].append(ch);
                    state = 1;
                  }
                  else
                  {
                    state = 5;
                  }
                }
              }
              break;
            }
          }
        }
      }
      if (ich == -1) {
        ret = -1;
      }
    }
    catch (IOException ex)
    {
      ret = -2;
    }
    return ret;
  }
  
  private boolean isIgnored(char ch)
  {
    return this.ignored.indexOf(ch) != -1;
  }
  
  private boolean isWhiteSpace(char ch)
  {
    return this.whiteSpaces.indexOf(ch) != -1;
  }
  
  private boolean isKeyValueDelimiter(char ch)
  {
    return this.keyValueDelimiters.indexOf(ch) != -1;
  }
  
  private boolean isCommentStart(char ch)
  {
    return this.commentStart.indexOf(ch) != -1;
  }
  
  private boolean isFieldDelimiter(char ch)
  {
    return this.fieldDelimiters.indexOf(ch) != -1;
  }
  
  public static void trimQuote(StringBuffer buff)
  {
    int last = buff.length() - 1;
    if (buff.charAt(0) == '\'')
    {
      if (buff.charAt(last) == '\'')
      {
        buff.setLength(last);
        buff.deleteCharAt(0);
      }
    }
    else if ((buff.charAt(0) == '"') && 
      (buff.charAt(last) == '"'))
    {
      buff.setLength(last);
      buff.deleteCharAt(0);
    }
  }
}
