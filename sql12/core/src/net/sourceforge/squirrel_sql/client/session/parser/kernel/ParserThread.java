package net.sourceforge.squirrel_sql.client.session.parser.kernel;

import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.schema.Table;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableQualifier;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParserThread
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ParserThread.class);

   private final ExecutorService _executorService;
   private ISession _session;
   private ParsingFinishedListener _parsingFinishedListener;
   private Future<?> _currentFuture;
   private ErrorInfo[] _errorInfos = new ErrorInfo[0];
   private TableAliasInfo[] _tableAliasInfos = new TableAliasInfo[0];

   private ParseTerminateRequestCheck _parseTerminateRequestCheck = () -> onCheckExitThreadRequested();
   private volatile boolean _exitThreadRequested = false;


   public ParserThread(ISession session)
   {
      _session = session;
      _executorService = Executors.newSingleThreadExecutor();
   }

   private void onCheckExitThreadRequested()
   {
      if (_exitThreadRequested)
      {
         throw new ParseTerminateRequestException();
      }
   }


   public void setParsingFinishedListener(ParsingFinishedListener parsingFinishedListener)
   {
      _parsingFinishedListener = parsingFinishedListener;
   }

   public void parseInBackground(String text)
   {
      if(_exitThreadRequested || null != _currentFuture && false == _currentFuture.isDone())
      {
         return;
      }

      _currentFuture = _executorService.submit(() -> onParse(text));
   }

   private void onParse(String text)
   {
      try
      {
         try
         {
            _onParse(text);
            _parsingFinishedListener.parsingFinished();
         }
         catch (ParseTerminateRequestException e)
         {
            // Parsing was requested to finish. We just stop and do nothing.
            //System.out.println("################## FINISHED PARSING ON REQUEST #########################");
         }
      }
      catch (Throwable e)
      {
         if(null != _parsingFinishedListener)
         {
            _parsingFinishedListener.parserExitedOnException(e);
         }
      }
   }

   private void _onParse(String text) throws ParseException
   {
      List<StatementBounds> statementBoundsList = StatementBoundsPrediction.getStatementBoundsList(text, _parseTerminateRequestCheck);

      _parseTerminateRequestCheck.check();

      ArrayList<ErrorInfo> errorInfosBuffer = new ArrayList<>();
      ArrayList<TableAliasInfo> tableAliasInfosBuffer = new ArrayList<>();

      for (StatementBounds statementBounds : statementBoundsList)
      {
         _parseTerminateRequestCheck.check();

         ParsingResult parsingResult = JSqlParserAdapter.executeParsing(statementBounds);

         _parseTerminateRequestCheck.check();

         List<TableAliasInfo> tableAliasInfosForCurrentStatement = new ArrayList<>();
         for (Table table : parsingResult.getTables())
         {
            _parseTerminateRequestCheck.check();

            TableQualifier tableQualifier = new TableQualifier(table.getFullyQualifiedName());

            ITableInfo[] tableInfos = _session.getSchemaInfo().getITableInfos(StringUtilities.stripDoubleQuotes(tableQualifier.getCatalog()),
                                                                              StringUtilities.stripDoubleQuotes(tableQualifier.getSchema()),
                                                                              StringUtilities.stripDoubleQuotes(tableQualifier.getTableName()));

            _parseTerminateRequestCheck.check();

            if(0 == tableInfos.length)
            {
               int beginPos = statementBounds.getBeginPos() + table.getASTNode().jjtGetFirstToken().absoluteBegin;
               int endPos = statementBounds.getBeginPos() + table.getASTNode().jjtGetFirstToken().absoluteEnd;
               errorInfosBuffer.add(new ErrorInfo(s_stringMgr.getString("parserthread.undefinedTable"), beginPos, endPos));
            }
            else if(null != table.getAlias())
            {
               tableAliasInfosForCurrentStatement.add(new TableAliasInfo(table.getAlias().getName(), table.getFullyQualifiedName(), statementBounds.getBeginPos(), statementBounds.getEndPos()));
            }
         }

         if(aliasesMayGotLostByParserErrors(tableAliasInfosForCurrentStatement, parsingResult))
         {
            tableAliasInfosForCurrentStatement = new HeuristicSQLAliasParser().parse(statementBounds, _session.getSchemaInfo());

//            System.out.println("####################### " + new Date());
//            System.out.println("##");
//            for (TableAliasInfo tableAliasInfo : tableAliasInfosForCurrentStatement)
//            {
//               System.out.println(tableAliasInfo.getAliasName() + " -> " + tableAliasInfo.getTableName());
//            }
         }

         tableAliasInfosBuffer.addAll(tableAliasInfosForCurrentStatement);

         for (ParseException parseError : parsingResult.getParseErrors())
         {
            _parseTerminateRequestCheck.check();

            int beginPos = statementBounds.getBeginPos() + parseError.currentToken.next.absoluteBegin;
            int endPos = statementBounds.getBeginPos() + parseError.currentToken.next.absoluteEnd;
            errorInfosBuffer.add(new ErrorInfo(parseError.getMessage(), beginPos, endPos));
         }
      }

      _errorInfos = errorInfosBuffer.toArray(new ErrorInfo[0]);
      _tableAliasInfos = tableAliasInfosBuffer.toArray(new TableAliasInfo[0]);
   }

   private boolean aliasesMayGotLostByParserErrors(List<TableAliasInfo> tableAliasInfos, ParsingResult parsingResult)
   {
      return 0 == tableAliasInfos.size() && 0 < parsingResult.getParseErrors().size();
   }


   public TableAliasInfo[] getTableAliasInfos()
   {
      return _tableAliasInfos;
   }

   public ErrorInfo[] getErrorInfos()
   {
      return _errorInfos;
   }

   public void exitThread()
   {
      _exitThreadRequested = true;
   }
}
