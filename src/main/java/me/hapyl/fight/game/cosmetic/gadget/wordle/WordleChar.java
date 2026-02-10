package me.hapyl.fight.game.cosmetic.gadget.wordle;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public enum WordleChar {
    
    A(
            "20afd777d557a207bac7aad8421fdf788d6685873c49515d55291e930699f",
            "2ac58b1a3b53b9481e317a1ea4fc5eed6bafca7a25e741a32e4e3c2841278c",
            "a67d813ae7ffe5be951a4f41f2aa619a5e3894e85ea5d4986f84949c63d7672e"
    ),
    B(
            "747899d511ba3a3bfe14541a6a4b77b033e44acd956896e7b6567429bf18d83c",
            "d4c711571e7e214ee78dfe4ee0e1263b92516e418de8fc8f3257ae0901431",
            "50c1b584f13987b466139285b2f3f28df6787123d0b32283d8794e3374e23"
    ),
    C(
            "eac0da9494b974bdbcb78861df96aa5aaa5da35c658721718ab26dc2ff67d87",
            "fff5aabead6feafaaecf4422cdd7837cbb36b03c9841dd1b1d2d3edb7825e851",
            "abe983ec478024ec6fd046fcdfa4842676939551b47350447c77c13af18e6f"
    ),
    D(
            "8943124bf6d5792511bb8ac5a7978e236b74fc7fc1d39af955ecedf97ab4",
            "893e622b581975792f7c119ec6f40a4f16e552bb98776b0c7ae2bdfd4154fe7",
            "3193dc0d4c5e80ff9a8a05d2fcfe269539cb3927190bac19da2fce61d71"
    ),
    E(
            "918641016d1d7cb406a863dde81f6bbd30371cf46d8f1420cd5ec6cdb64ef7",
            "a157d65b19921c760ff4910b3404455b9c2ee36afc202d8538baefec676953",
            "dbb2737ecbf910efe3b267db7d4b327f360abc732c77bd0e4eff1d510cdef"
    ),
    F(
            "3c4c399b5e7522a17d35eb53fe5eb767a3b38ea6e5cd33b4438020fc5a848a",
            "c54cf261b2cd6ab54b0c624f8f6ff565a7b63e28e3b50c6dbfb52b5f0d7cf9f",
            "b183bab50a3224024886f25251d24b6db93d73c2432559ff49e459b4cd6a"
    ),
    G(
            "a7ca8b9c7169a743f82e8e9fd68f11c0f31070a53c92863884e673137fbaf1",
            "d3c9f8a74ca01ba8c54de1edc82e1fc07a83923e66574b6ffe606919240c6",
            "1ca3f324beeefb6a0e2c5b3c46abc91ca91c14eba419fa4768ac3023dbb4b2"
    ),
    H(
            "41595773048de9c411896fae9ba9d48d55b71677199701064984b42cd2a17d8",
            "f8c58c509034617bf81ee0db9be0ba3e85ca15568163914c87669edb2fd7",
            "31f3462a473549f1469f897f84a8d4119bc71d4a5d852e85c26b588a5c0c72f"
    ),
    I(
            "6e5dc3d67d7cc47ee95c20c4202ecd91d91dc63660d8144f81628881d7fde2",
            "4246323c9fb319326ee2bf3f5b63ec3d99df76a12439bf0b4c3ab32d13fd9",
            "46178ad51fd52b19d0a3888710bd92068e933252aac6b13c76e7e6ea5d3226"
    ),
    J(
            "27b38b21528f2c463a48ed5c8937f85dd16cb98bf96042897948e74b7cc631",
            "c58456cd9bb8a7e978591ae0cb26af1aadad4fa7a16725b295145e09bed8064",
            "3a79db9923867e69c1dbf17151e6f4ad92ce681bcedd3977eebbc44c206f49"
    ),
    K(
            "abe3ea814dbc707a39272d6ae336c068844ead52c7b7d2ee1ba4d50bbd9",
            "af49fb708369e7bc2944ad706963fb6ac6ce6d4c67081ddadecfe5da51",
            "9461b38c8e45782ada59d16132a4222c193778e7d70c4542c9536376f37be42"
    ),
    L(
            "3bba9a55fee9cad05f1e947e6d475520fbe51137a532796135203ece3d269",
            "8c84f75416e853a74f6c70fc7e1093d53961879955b433bd8c7c6d5a6df",
            "319f50b432d868ae358e16f62ec26f35437aeb9492bce1356c9aa6bb19a386"
    ),
    M(
            "4c1713d90ca5998716eaa7dd302f6c8d672595dcc6956c2e20e94184bae2",
            "31fde91b19b9309913724fea9e85311271c67bcb78578d461bf65d9613074",
            "49c45a24aaabf49e217c15483204848a73582aba7fae10ee2c57bdb76482f"
    ),
    N(
            "34ea15d10d167c355e941f98f8b5e1cf716d5dea7fafcc69f9bcdb52a8f34e",
            "1c7c972e6785d6b0aceb779abdd7702d98341c24c2a71e702930eca58055",
            "35b8b3d8c77dfb8fbd2495c842eac94fffa6f593bf15a2574d854dff3928"
    ),
    O(
            "e8f47923861c275311ab69b03e2751224ff394e44e3b3c8db5225ff",
            "8073bb44f9345f9bb31a679027e7939e461842a8c27486d7a6b842c39eb38c4e",
            "d11de1cadb2ade61149e5ded1bd885edf0df6259255b33b587a96f983b2a1"
    ),
    P(
            "4f5b409417ff2e777d5e83cecd2856d2ccc68dffdef940154e945ace6d9ccc",
            "64b231a8d55870cfb5a9f4e65db06dd7f8e34282f1416f95878b19acc34ac95",
            "a0a7989b5d6e621a121eedae6f476d35193c97c1a7cb8ecd43622a485dc2e912"
    ),
    Q(
            "618c1061f62f98cfac3d7549b983c43f27c2da229ddc266f51677ca9337",
            "ffedd6f9efdb156b86935699b2b4834df0f5d214513c01d38af3bd031cbcc92",
            "43609f1faf81ed49c5894ac14c94ba52989fda4e1d2a52fd945a55ed719ed4"
    ),
    R(
            "c91d46f1844341bc94d1f8f4519d3f7c6e2fa718b664b935661a2782a670e2",
            "c03a1cd583cbbffde08f943e56ac3e3afafecaede834221a81e6db6c64667f7d",
            "a5ced9931ace23afc351371379bf05c635ad186943bc136474e4e5156c4c37"
    ),
    S(
            "3ed9d96c393e38a36b61aa3c859ede5eb744ef1e846d4f7d0ecbd6588a021",
            "b6572e655725d78375a9817eb9ee8b37829ca1fea93b6095cc7aa19e5eac",
            "3e41c60572c533e93ca421228929e54d6c856529459249c25c32ba33a1b1517"
    ),
    T(
            "d23bae41d1333426b625e141d9d4806b6f4641d6754d7e19e4bbefa37fe513",
            "708c9ef3a3751e254e2af1ad8b5d668ccf5c6ec3ea2641877cba575807d39",
            "1562e8c1d66b21e459be9a24e5c027a34d269bdce4fbee2f7678d2d3ee4718"
    ),
    U(
            "f98edadf35c82be08cde43e52da8830f87f9288992088a699fd2ecc4be073",
            "55a6e3ae5ae625923524838fac9fef5b42527f5027c9ca149e6c207792eb",
            "607fbc339ff241ac3d6619bcb68253dfc3c98782baf3f1f4efdb954f9c26"
    ),
    V(
            "a86c97159cd658858d12b833d1671b84529e1f116d8be379d43824cb17963bb",
            "975121f7d9c68da0e5b6a96ac615298b12b2ee5bd19989436ee647879da5b",
            "cc9a138638fedb534d79928876baba261c7a64ba79c424dcbafcc9bac7010b8"
    ),
    W(
            "3d31ceb43e3908928c0ce9ceeecf79b8d33c54faeba8d6174d80ac3641c3ba",
            "67e165c3edc5541d4654c4728871e6908f613fc0ec46e823c96eac82ac62e62",
            "269ad1a88ed2b074e1303a129f94e4b710cf3e5b4d995163567f68719c3d9792"
    ),
    X(
            "6e8646bb76f0c6d03a637a9b4975b3a85276eb20d6f16bdb91866c8c3c8285c5",
            "1919d1594bf809db7b44b3782bf90a69f449a87ce5d18cb40eb653fdec2722",
            "5a6787ba32564e7c2f3a0ce64498ecbb23b89845e5a66b5cec7736f729ed37"
    ),
    Y(
            "831d3df99578c79e0fd64b6115b6b44ed4dcab4a987be7c7e91baa61886e0eb",
            "e35424bb86305d7747604b13e924d74f1efe38906e4e458dd18dcc67b6ca48",
            "c52fb388e33212a2478b5e15a96f27aca6c62ac719e1e5f87a1cf0de7b15e918"
    ),
    Z(
            "76157b9c101312838b128e71d55bfc9ad4e26b5919a73807b13466a76d5e0",
            "4e91200df1cae51acc071f85c7f7f5b8449d39bb32f363b0aa51dbc85d133e",
            "90582b9b5d97974b11461d63eced85f438a3eef5dc3279f9c47e1e38ea54ae8d"
    );
    
    private final ItemStack letterCorrect;
    private final ItemStack letterIncorrect;
    private final ItemStack letterPresent;
    
    WordleChar(String correct, String incorrect, String present) {
        letterCorrect = ItemBuilder
                .playerHeadUrl(correct)
                .setName("&a&l" + name())
                .addSmartLore("This letter is in the word and in the correct spot.")
                .build();
        letterIncorrect = ItemBuilder
                .playerHeadUrl(incorrect)
                .setName("&7&l" + name())
                .addSmartLore("This letter is not in the word in any spot.")
                .build();
        letterPresent = ItemBuilder
                .playerHeadUrl(present)
                .setName("&e&l" + name())
                .addSmartLore("This letter is in the word but in the wrong spot.")
                .build();
    }
    
    @Nonnull
    public ItemStack letterCorrect() {
        return letterCorrect;
    }
    
    @Nonnull
    public ItemStack letterIncorrect() {
        return letterIncorrect;
    }
    
    @Nonnull
    public ItemStack letterPresent() {
        return letterPresent;
    }
    
    @Nonnull
    public ItemStack letterOf(@Nonnull CharacterValue value) {
        return switch (value) {
            case CORRECT -> letterCorrect;
            case WRONG_POSITION -> letterPresent;
            case INCORRECT -> letterIncorrect;
            case HAS_NOT_GUESSED -> throw new IllegalStateException("Must not call for " + value);
        };
    }
    
    @Nonnull
    public static WordleChar byChar(@Nonnull Character c) {
        for (WordleChar value : values()) {
            if (value.name().equalsIgnoreCase(c.toString())) {
                return value;
            }
        }
        
        throw new IllegalArgumentException("Illegal char: " + c);
    }
    
}
