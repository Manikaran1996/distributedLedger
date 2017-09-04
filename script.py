from mininet.topo import Topo,LinearTopo,SingleSwitchTopo
from mininet.net import Mininet
from mininet.node import OVSBridge
from mininet.term import makeTerm
from mininet.link import TCLink
from sys import stdin
import os
class MyTopo(Topo):
    def build(self, n):
        sw = self.addSwitch('S1')
        for h in range(n):
            ho = self.addHost('h%s' % (h + 1))
            self.addLink(ho, sw, bw=100,loss=0)
n=input("Enter number of nodes in the network : ")
topo = MyTopo(n)
#topo=SingleSwitchTopo(n)
net = Mininet(topo,link=TCLink)
net.start()
p={}
os.chdir(os.getcwd()+"/bin")
ini=input("\nEnter number of nodes initially to be run : ")
for i in range(1,ini+1):
	h=net.get("h%d"%(i))
	p[i]=makeTerm(h,title=h.name,term='xterm',cmd='java node.DriverProgram %d %s %d'%(i,h.IP(),ini)) 
print "\nIntialization done!\n"
print "Dynamic node addition "
print "Use: 1 x to add node x"
print "Use: 2 x to remove node x"
print "Use: 0 to exit" 	
while True:
	l=stdin.readline().rstrip().split(" ")
	if l[0]=='0':
		print "Closing hosts" 
		break	
	t=int(l[1])
	if l[0]=='1': 
		if t in p: 
			print "%d is already running"%(t)
			continue
				
		h=net.get("h%d"%(t))
		p[t]=makeTerm(h,title=h.name,term='xterm',cmd='java node.DriverProgram %d %s %d'%(t,h.IP(),len(p)))
		print "%d is now running"%(t)
	elif l[0]=='2':
		if t not in p:
			print "%d is not running"%(t)
			continue
		p[t][0].terminate()
		#p[t][1].terminate()
		print "%d terminated"%(t)
		p.pop(t)			
for i in p: 
	p[i][0].terminate()
	#p[i][1].terminate()
net.stop()
