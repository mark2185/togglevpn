package main

import (
	"fmt"
	"net/http"
	"os/exec"
	"strings"

	"github.com/gin-gonic/gin"
)

func runCommand(cmd string, args ...string) string {
	command := exec.Command(cmd, args...)
	out, err := command.CombinedOutput()
	if err != nil {
		fmt.Println("Uh-oh, panic: ", err)
	}
	return strings.TrimSpace(string(out))
}

func turnOn(c *gin.Context) {
	out := runCommand("systemctl", "start", "openvpn.service")
	fmt.Println(out)
}

func turnOff(c *gin.Context) {
	out := runCommand("systemctl", "stop", "openvpn.service")
	fmt.Println(out)
}

func status(c *gin.Context) {
	out := runCommand("systemctl", "is-active", "openvpn.service")
	status := "on"
	if out == "inactive" {
		status = "off"
	}
	c.String(http.StatusOK, status)
	fmt.Println(out)
}

func main() {
	router := gin.Default()
	router.GET("/on", turnOn)
	router.GET("/off", turnOff)
	router.GET("/status", status)
	router.Run("0.0.0.0:8081")
}
