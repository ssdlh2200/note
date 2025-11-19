/*
查询第二高的薪水，那是不是可以引申查询第三高，第四高的薪水呢？
100
100
100

*/
select salary from employee order by salary desc;

select max(salary) from employee;

select
(select salary from employee
              where salary < (select max(salary) from employee) order by salary desc limit 1) SecondHighestSalary;


SELECT DISTINCT Salary FROM Employee
ORDER BY Salary DESC
LIMIT 1 OFFSET 1;

select id, (select salary from employee limit 1) from employee;

select id, 1 from employee;