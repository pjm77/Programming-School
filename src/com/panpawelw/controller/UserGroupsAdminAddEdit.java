package com.panpawelw.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.panpawelw.DAO.RealUserGroupDAO;
import com.panpawelw.DAO.UserGroupDAO;
import com.panpawelw.misc.ValidateParameter;
import com.panpawelw.misc.DbUtils;
import com.panpawelw.model.UserGroup;

@WebServlet("/addeditgroup")
public class UserGroupsAdminAddEdit extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private UserGroupDAO userGroupDAO;

    public UserGroupsAdminAddEdit() {
        super();
    }

    public void init() {
        if(userGroupDAO == null) this.userGroupDAO = new RealUserGroupDAO(DbUtils.initDB());
    }

    public void setUserGroupDAO(UserGroupDAO userGroupDAO) {
        this.userGroupDAO = userGroupDAO;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        int groupId = ValidateParameter.checkInt(idParam, "Incorrect group Id!");
        if(groupId < 0) getServletContext().getRequestDispatcher("/groupsadminpanel")
                .forward(request, response);
        if(groupId == 0) {
            request.setAttribute("group", new UserGroup(0));
            request.setAttribute("button", "Add group");
        } else if (groupId > 0) {
            request.setAttribute("group", userGroupDAO.loadUserGroupById(groupId));
            request.setAttribute("button", "Edit group");
        }
        getServletContext().getRequestDispatcher("/jsp/usergroupsadminaddeditview.jsp").forward(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        String groupName = request.getParameter("group_name");
        int groupId = ValidateParameter.checkInt(idParam, "Incorrect group Id!");
        if (groupName != null && !groupName.equals("") && groupId >= 0) {
            UserGroup userGroup = new UserGroup();
            if (groupId != 0) {
                userGroup = userGroupDAO.loadUserGroupById(groupId);
            }
            userGroup.setName(groupName);
            userGroupDAO.saveUserGroupToDB(userGroup);
        } else {
            request.setAttribute("errorMessage", "Group name can't be empty!");
        }
        getServletContext().getRequestDispatcher("/groupsadminpanel").forward(request, response);
    }
}