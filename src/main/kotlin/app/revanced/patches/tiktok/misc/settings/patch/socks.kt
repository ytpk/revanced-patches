import org.jf.dexlib2.iface.instruction.Instruction
import org.jf.dexlib2.Opcode

@Patch
@Name("SOCKS Proxy")
@Description("Adds SOCKS5 proxy support to TikTok.")
class SocksProxyPatch : BytecodePatch() {

    override fun execute(context: BytecodeContext): PatchResult {

        val networkClass = context.classFinder.findClass(
            "com.ss.android.ugc.trill.network.TrillNetworkService"
        ) ?: return PatchResultError("Network class not found")
        
        val initClientMethod = networkClass.findMethod("initOkHttpClient") ?: return PatchResultError("Init method not found")

        initClientMethod.mutableMethod.apply {
            
            // Импортировать классы
            importClasses("okhttp3.OkHttpClient", "java.net.Proxy")
            
            // Добавить код
            addInstructions(
                Opcodes.ALOAD_0,
                Instruction("getOkHttpClientBuilder()Lokhttp3/OkHttpClient\$Builder;"),
                Instruction("invoke-virtual {v1}, Lokhttp3/OkHttpClient\$Builder;->proxy(Ljava/net/Proxy;)Lokhttp3/OkHttpClient\$Builder;"),
                Instruction("ldc \"socks5://127.0.0.1:1080\""),
                Instruction("invokestatic java/net/Proxy->SOCKS(Ljava/net/SocketAddress;)Ljava/net/Proxy;"),
                Instruction("invoke-virtual {v1, v2}, Lokhttp3/OkHttpClient\$Builder;->proxy(Ljava/net/Proxy;)Lokhttp3/OkHttpClient\$Builder;"),
                Instruction("invoke-virtual {v1}, Lokhttp3/OkHttpClient\$Builder;->build()Lokhttp3/OkHttpClient;")
            )
            
        }

        return PatchResultSuccess()
    }

}
