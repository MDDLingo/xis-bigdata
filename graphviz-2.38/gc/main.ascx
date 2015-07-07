<%@ Control language="vb" AutoEventWireup="false" Explicit="True" Inherits="DotNetNuke.UI.Skins.Skin" %>
<%@ Register TagPrefix="dnn" TagName="LANGUAGE" Src="~/Admin/Skins/Language.ascx" %>
<%@ Register TagPrefix="dnn" TagName="LOGO" Src="~/Admin/Skins/Logo.ascx" %>
<%@ Register TagPrefix="dnn" TagName="SEARCH" Src="~/Admin/Skins/Search.ascx" %>
<%@ Register TagPrefix="dnn" TagName="BREADCRUMB" Src="~/Admin/Skins/BreadCrumb.ascx" %>
<%@ Register TagPrefix="dnn" TagName="USER" Src="~/Admin/Skins/User.ascx" %>
<%@ Register TagPrefix="dnn" TagName="LOGIN" Src="~/Admin/Skins/Login.ascx" %>
<%@ Register TagPrefix="dnn" TagName="PRIVACY" Src="~/Admin/Skins/Privacy.ascx" %>
<%@ Register TagPrefix="dnn" TagName="TERMS" Src="~/Admin/Skins/Terms.ascx" %>
<%@ Register TagPrefix="dnn" TagName="COPYRIGHT" Src="~/Admin/Skins/Copyright.ascx" %>
<%@ Register TagPrefix="dnn" TagName="LINKTOMOBILE" Src="~/Admin/Skins/LinkToMobileSite.ascx" %>
<%@ Register TagPrefix="dnn" TagName="META" Src="~/Admin/Skins/Meta.ascx" %>
<%@ Register TagPrefix="dnn" TagName="MENU" src="~/DesktopModules/DDRMenu/Menu.ascx" %>
<%@ Register TagPrefix="dnn" TagName="Nav" Src="~/Admin/Skins/nav.ascx" %>
<%@ Register TagPrefix="dnn" Namespace="DotNetNuke.Web.Client.ClientResourceManagement" Assembly="DotNetNuke.Web.Client" %>
<dnn:Meta runat="server" Name="viewport" Content="initial-scale=1.0,width=device-width" />

<dnn:DnnCssInclude ID="bootstrapCSS" runat="server" FilePath="../gc/css/bootstrap.min.css" PathNameAlias="SkinPath" Priority="8"/>
<dnn:DnnJsInclude ID="bootstrapJS" runat="server"  FilePath="../gc/js/bootstrap.min.js" PathNameAlias="SkinPath" />

<dnn:DnnCssInclude ID="cssFontJura" runat="server" FilePath="http://fonts.googleapis.com/css?family=Open+Sans" />

<div class="container" style="height:70px;padding-top:10px;background:#576b6f;">
    <div id="MainMenuPane" class="col-md-12">

        <nav class="navbar navbar-default navbar-static-top">
            <div class="container">
                <div class="navbar-header">
                    <dnn:LOGO runat="server" ID="logo1" />
                    <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                </div>
                <div id="navbar" class="collapse navbar-collapse">
                    <ul class="nav navbar-nav">
                        <li>
                            <dnn:MENU ID="MENU1" MenuStyle="bootstrapMenu" runat="server"></dnn:MENU>
                        </li>
                    </ul>
                    <ul class="nav navbar-nav navbar-right">
                        <li>
                            <dnn:USER runat="server" ID="USER1" CssClass="user" />
                        </li>
                        <li>
                            <dnn:LOGIN ID="dnnLogin" class="inline" runat="server" LegacyMode="true" />
                        </li>
                    </ul>
                </div>
            </div>
        </nav>

    </div>
    <%--<div id="UserPane" class="col-md-4" runat="server">
        <ul class="nav navbar-nav navbar-right">
            <li><dnn:USER runat="server" id="USER1" CssClass="user" /></li>
            <li><dnn:LOGIN ID="dnnLogin" class="inline" runat="server" LegacyMode="true" /></li>
        </ul>
    </div>--%>
</div>
<div class="container" style="height:80px;background:#fff;">
    <div id="BreadcrumbsPane" class="col-md-12 " style="height:40px;float:left;text-align:left;">
        <dnn:BREADCRUMB runat="server" ID="dnnBreadcrumb" />
    </div>
    <div id="SecondMenuPane" class="col-md-12 text-right" style="height:40px;float:right;text-align:right;" runat="server"></div>
</div>

<div class="container" style="border-top:1px solid #dde4e5;background:#fafcfc;">
    <div id="ContentPane" class="col-md-12" runat="server"></div>
    <div id="grid2_01_col01" class="col-md-6" runat="server"></div>
    <div id="grid2_01_col02" class="col-md-6" runat="server"></div>
    <div id="grid2_02_col01" class="col-md-5" runat="server"></div>
    <div id="grid2_02_col02" class="col-md-7" runat="server"></div>
    <div id="grid2_03_col01" class="col-md-7" runat="server"></div>
    <div id="grid2_03_col02" class="col-md-5" runat="server"></div>
    <div id="grid2_04_col01" class="col-md-4" runat="server"></div>
    <div id="grid2_04_col02" class="col-md-8" runat="server"></div>
    <div id="grid2_05_col01" class="col-md-8" runat="server"></div>
    <div id="grid2_05_col02" class="col-md-4" runat="server"></div>
    <div id="grid3_01_col01" class="col-md-4" runat="server"></div>
    <div id="grid3_01_col02" class="col-md-4" runat="server"></div>
    <div id="grid3_01_col03" class="col-md-4" runat="server"></div>
    <div id="grid4_01_col01" class="col-md-3" runat="server"></div>
    <div id="grid4_01_col02" class="col-md-3" runat="server"></div>
    <div id="grid4_01_col03" class="col-md-3" runat="server"></div>
    <div id="grid4_01_col04" class="col-md-3" runat="server"></div>
    <div id="grid_6_01_col01" class="col-md-2" runat="server"></div>
    <div id="grid_6_01_col02" class="col-md-2" runat="server"></div>
    <div id="grid_6_01_col03" class="col-md-2" runat="server"></div>
    <div id="grid_6_01_col04" class="col-md-2" runat="server"></div>
    <div id="grid_6_01_col05" class="col-md-2" runat="server"></div>
    <div id="grid_6_01_col06" class="col-md-2" runat="server"></div>
</div>