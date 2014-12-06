<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.TimeZone" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="en">
    <head>

        <!-- Bootswatch "Paper" theme -->
        <link type="text/css" rel="stylesheet" href="/bootstrap.css" />

        <!-- JQuery for dropdowns and such -->
        <script src="/jquery-2.1.0.min.js"></script>

        <!-- Latest compiled and minified JavaScript for Bootstrap -->
        <script src="/bootstrap.min.js"></script>

        <!-- Bootstrap Glyphicons -->
        <link href="/bootstrap-glyphicons.css" rel="stylesheet">

        <!-- Timeago JQuery plugin -->
        <!-- <script src="http://timeago.yarp.com/jquery.timeago.js"></script> -->
    </head>

    <body>

        <!-- Navbar -->
        <br />
        <div class="container">
            <div class="navbar navbar-inverse">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle"
                            data-toggle="collapse" data-target=".navbar-responsive-collapse">
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand" href="/">SuperTicTacToe</a>
                </div>
                <div class="navbar-collapse collapse navbar-responsive-collapse">
                    <ul class="nav navbar-nav">
                    </ul>
                    <ul class="nav navbar-nav navbar-right">
                        <li class="dropdown">

                            <!-- User login/logout button -->
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">Menu <b class="caret"></b></a>
                            <ul class="dropdown-menu">
                                <li class="divider"></li>
                                <li><a href="#" data-toggle="modal" data-target=".subscribe-form">Subscribe</a></li>
                                <li><a href="#" data-toggle="modal" data-target=".unsubscribe-form">Unsubscribe</a></li>
                            </ul>
                        </li>
                    </ul>

                </div>
            </div>
        </div>

        <div class="container">
            <!-- Alert Area -->
            <div id="alert-area"></div>


            <!-- Main body -->
            <div class="main">

                <!-- The main game -->
                <div class="row">
                    <div class="col-md-9">
                        <div class="panel panel-primary">
                            <div class="panel-body">
  	                        <canvas id="canvas_1" width="476" height="476">
			            Canvas Tag not supported
		                </canvas>

                            </div>
                        </div>
                    </div>

                    <!-- The sidebar -->
                    <div class="col-md-3">
                        <div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title">Options</h3>
                            </div>
                            <div class="panel-body">
                                <div class="form-group">
                                    <div class="col-lg-2">
                                        <p><strong>Opponent:</strong></p>
                                    </div><br/>
                                    <div class="col-lg-10">
                                        <div class="radio">
                                            <label><input type="radio" name="opponentRadios" id="opponentRadioHuman" value="Human" checked="">Human</label>
                                        </div>
                                        <div class="radio">
                                            <label><input type="radio" name="opponentRadios" id="oponentRadioAI" value="AI">AI</label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </div>

            <div class="jumbotron">
                <h1><font color="#2196f3">Super</font>TicTacToe</h1>

                <h2>About SuperTicTacToe:</h2>
                <p>SuperTicTacToe is a modern take on the classic Tic Tac Toe...</p>
                <h2>How to Play:</h2>
                <p>SuperTicTacToe is played...</p>

                <!-- <ul class="nav nav-tabs"">
                <li class="active"><a href="#about-supertictactoe" data-toggle="tab">About SuperTicTacToe</a></li>
                <li><a href="#how-to-play" data-toggle="tab">How to Play</a></li>
                </ul>
                <div id="lowfold-tabs" class="tab-content">
                <div class="tab-pane fade active in" id="about-supertictactoe">
                <h2>About SuperTicTacToe:</h2>
                <p>SuperTicTacToe is a modern take on the classic Tic Tac Toe...</p>
                </div>
                <div class="tab-pane fade" id="how-to-play">
                <h2>How to Play:</h2>
                <p>SuperTicTacToe is played...</p>
                </div>
                </div> -->
                </div>

            
            <footer>
                <div class="row">
                    <div class="col-lg-12 text-center">
                        <p>This is the footer</p>
                    </div>
                </div>
            </footer>
        </div>
    </body>
</html>